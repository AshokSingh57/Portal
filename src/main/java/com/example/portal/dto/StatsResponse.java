package com.example.portal.dto;

import java.util.Map;

public class StatsResponse {
    private boolean success;
    private Map<String, Object> stats;

    public StatsResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public Map<String, Object> getStats() { return stats; }
    public void setStats(Map<String, Object> stats) { this.stats = stats; }
}
