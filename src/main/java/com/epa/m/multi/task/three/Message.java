package com.epa.m.multi.task.three;

public class Message {
    private String topic;
    private String payload;

    public Message(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (topic != null ? !topic.equals(message.topic) : message.topic != null) return false;
        return payload != null ? payload.equals(message.payload) : message.payload == null;
    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}