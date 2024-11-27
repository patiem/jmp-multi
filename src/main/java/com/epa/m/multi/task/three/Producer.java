package com.epa.m.multi.task.three;

public class Producer implements Runnable {
    private final MessageBus messageBus;
    private final String topic;

    public Producer(MessageBus messageBus, String topic) {
        this.messageBus = messageBus;
        this.topic = topic;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String payload = "Message@"+ topic + "$" + System.nanoTime();
            messageBus.publish(new Message(topic, payload));
            System.out.println(this + ": Produced: " + payload);

            try {
                // Simulate time to produce the message
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(this + ": Producer interrupted");
                break;
            }
        }
    }
}
