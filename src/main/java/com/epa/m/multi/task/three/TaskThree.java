package com.epa.m.multi.task.three;

public class TaskThree {

    public static void main(String[] args) {
        MessageBus bus = new MessageBus();
        String topic1 = "XXX_topic";
        String topic2 = "YYY_topic";

        new Thread(new Producer(bus, topic1)).start();
        new Thread(new Producer(bus, topic2)).start();

        new Thread(new Consumer(bus, topic1)).start();
        new Thread(new Consumer(bus, topic2)).start();
    }
}
