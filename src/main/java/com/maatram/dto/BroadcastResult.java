package com.maatram.dto;

public record BroadcastResult(int sent, int failed, int recipients, String status) {}
