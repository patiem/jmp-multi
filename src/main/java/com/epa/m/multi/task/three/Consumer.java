package com.epa.m.multi.task.three;

public class Consumer implements Runnable {
    private final MessageBus messageBus;
    private final String topic;

    public Consumer(MessageBus messageBus, String topic) {
        this.messageBus = messageBus;
        this.topic = topic;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = messageBus.consume(topic);
                System.out.println(this + ": Consumed: " + message.getPayload());
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(this + ": Consumer interrupted");
            }
        }
    }
}
