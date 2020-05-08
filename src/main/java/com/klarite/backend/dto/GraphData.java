package com.klarite.backend.dto;

import java.util.Map;

public class GraphData {
    private String name;
    private Integer value;
    private Map<String, Integer> extra;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Map<String, Integer> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Integer> extra) {
        this.extra = extra;
    }
}
