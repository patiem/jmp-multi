package com.epa.m.multi.task.three;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MessagingSystemTests {
    @Mock private MessageBus messageBus;
    private final String topic = "testTopic";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void consumerShouldConsumeMessages() throws InterruptedException {
        Message message = new Message(topic, "Hello World");
        when(messageBus.consume(topic)).thenReturn(message);

        Consumer consumer = new Consumer(messageBus, topic);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        // Allow some time for consumption
        Thread.sleep(100);

        // Verify consume was called on the message bus
        verify(messageBus, atLeastOnce()).consume(topic);

        // Interrupt the consumer thread to stop it
        consumerThread.interrupt();
        consumerThread.join();
    }

    @Test
    public void producerShouldProduceMessages() throws InterruptedException {
        Producer producer = new Producer(messageBus, topic);
        Thread producerThread = new Thread(producer);
        producerThread.start();

        // Allow some time for production
        Thread.sleep(1000);

        // Verify publish was called on the message bus
        verify(messageBus, atLeastOnce()).publish(any(Message.class));

        // Interrupt the producer thread to stop it
        producerThread.interrupt();
        producerThread.join();
    }

    @Test
    public void messageBusShouldHandlePublishAndConsume() {
        MessageBus messageBus = new MessageBus();
        Message message = new Message(topic, "testMessage");

        // Test publishing
        messageBus.publish(message);

        // Test consuming
        Message result = messageBus.consume(topic);

        assertNotNull(result);
        assertEquals(message, result);
    }
}