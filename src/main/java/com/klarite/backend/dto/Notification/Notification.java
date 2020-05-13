package com.klarite.backend.dto.Notification;

abstract public class Notification {
    private Long id;
    private Boolean active;
    private int type;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String requesterName) {
        this.senderName = requesterName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long observerId) {
        this.receiverId = observerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String observerName) {
        this.receiverName = observerName;
    }

    public void init(Long id, Boolean isActive, Long receiverId, String receiverName, Long senderId, String senderName, String payload) {
        setId(id);
        setActive(isActive);
        setReceiverId(receiverId);
        setReceiverName(receiverName);
        setSenderId(senderId);
        setSenderName(senderName);
        parseJSONString(payload);
    }

    public abstract String fetchPayload();

    public abstract void parseJSONString(String payload);
}