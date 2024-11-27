package com.epa.m.multi.task.three;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class MessageBus {
    private final HashMap<String, Queue<Message>> queues = new HashMap<>();

    public synchronized void publish(Message message) {
        queues.putIfAbsent(message.getTopic(), new LinkedList<>());
        queues.get(message.getTopic()).offer(message);
        notifyAll();
    }

    public synchronized Message consume(String topic) {
        while (!queues.containsKey(topic) || queues.get(topic).isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            }
        }
        return queues.get(topic).poll();
    }
}
