package com.klarite.backend.dto;

import java.util.List;

public class ReadNotification {
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}