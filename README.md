# lab14-demo — PodDisruptionBudget availability demo

`pdb-demo` reports its own pod and node name (`/api/status`) and exposes two demo-only chaos endpoints
(`/api/chaos/unready`, `/api/chaos/recover`) that flip a single pod's readiness on command — used to make
PodDisruptionBudget's eviction-blocking behavior reproducible without racing real node-drain timing.

## Local sanity check (no Kubernetes)

```bash
docker compose up --build
curl http://localhost:8080/api/status
curl -X POST http://localhost:8080/api/chaos/unready
curl http://localhost:8080/api/status   # "ready": false
curl -X POST http://localhost:8080/api/chaos/recover
```

## Run on a multi-node Minikube cluster

```bash
minikube start --nodes=3 --cpus=2 --memory=4096

eval $(minikube docker-env)
docker build -t pdb-demo:1.0.0 ./pdb-demo
minikube image load pdb-demo:1.0.0   # propagate the image to every node

kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/pdb.yaml
kubectl rollout status deployment/pdb-demo

kubectl get pods -l app=pdb-demo -o wide   # confirm one pod per node
kubectl get pdb pdb-demo-pdb
```

## Drain a node — PDB allows it

```bash
# Pick a worker node: the control-plane node also runs kube-system/storage-provisioner,
# a bare pod with no controller that makes `kubectl drain` refuse before PDB is even consulted.
NODE=$(kubectl get nodes -l '!node-role.kubernetes.io/control-plane' -o jsonpath='{.items[0].metadata.name}')
kubectl drain "$NODE" --ignore-daemonsets --delete-emptydir-data --timeout=90s
kubectl get pods -l app=pdb-demo -o wide -w   # watch it reschedule and go Ready
kubectl uncordon "$NODE"
```

## Force a second pod unready, then try to drain — PDB blocks it

```bash
CP_NODE=$(kubectl get nodes -l node-role.kubernetes.io/control-plane -o jsonpath='{.items[0].metadata.name}')
POD=$(kubectl get pods -l app=pdb-demo --field-selector spec.nodeName="$CP_NODE" -o jsonpath='{.items[0].metadata.name}')
kubectl port-forward "pod/$POD" 8081:8080 &
curl -X POST http://localhost:8081/api/chaos/unready

kubectl get pdb pdb-demo-pdb   # ALLOWED DISRUPTIONS drops to 0

OTHER_NODE=$(kubectl get nodes -l '!node-role.kubernetes.io/control-plane' -o jsonpath='{.items[1].metadata.name}')
kubectl drain "$OTHER_NODE" --ignore-daemonsets --delete-emptydir-data --timeout=30s
# error: Cannot evict pod as it would violate the pod's disruption budget.

curl -X POST http://localhost:8081/api/chaos/recover
kubectl drain "$OTHER_NODE" --ignore-daemonsets --delete-emptydir-data --timeout=90s
kubectl uncordon "$OTHER_NODE"
```

Full walkthrough — voluntary vs involuntary disruption, the Eviction API, and why PDB does *not* gate
rolling updates or HPA scale-down — is in `post.md` in the parent directory.
