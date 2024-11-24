package com.epa.m.multi.task.three;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskThree {

        public static void main(String[] args) {
            MessageBus bus = new MessageBus();
            String topic1 = "XXX_topic";
            String topic2 = "YYY_topic";

            // Start producers
            new Thread(new Producer(bus, topic1)).start();
            new Thread(new Producer(bus, topic2)).start();

            // Start consumers
            new Thread(new Consumer(bus, topic1)).start();
            new Thread(new Consumer(bus, topic2)).start();
        }
    }