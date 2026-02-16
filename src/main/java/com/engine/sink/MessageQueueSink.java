package com.engine.sink;

import com.engine.ingestion.DataRecord;

public class MessageQueueSink implements DataSink {
    @Override
    public void send(DataRecord record) {
        // Simulates publishing to a distributed topic
        System.out.println("[MQ Sink] Published message to topic 'data-fanout': " + record.id());
    }

    @Override
    public String getServiceType() {
        return "MQ";
    }
}