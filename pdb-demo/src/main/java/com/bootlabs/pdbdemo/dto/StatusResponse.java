package com.bootlabs.pdbdemo.dto;

public record StatusResponse(String pod, String node, boolean ready) {
}
