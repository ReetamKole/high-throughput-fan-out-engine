package com.engine.sink;

import com.engine.ingestion.DataRecord;
import com.engine.transformation.TransformationStrategy;
import com.engine.transformation.JsonTransformer;

public class RestSink implements DataSink {
    private final TransformationStrategy transformer = new JsonTransformer();

    @Override
    public void send(DataRecord record) {
        String payload = transformer.transform(record);
        // Simulate network latency
        try {
            Thread.sleep(10); 
            System.out.println("[REST Sink] Sent: " + payload);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    @Override
public String getServiceType() {
    return "REST";
}
}