package com.engine.sink;

import com.engine.ingestion.DataRecord;
import com.engine.transformation.TransformationStrategy;
// Note: In a full impl, you'd create an AvroTransformer here
import com.engine.transformation.JsonTransformer; 

public class DatabaseSink implements DataSink {
    @Override
    public void send(DataRecord record) {
        // Simulating an async DB write
        System.out.println("[DB Sink] Upserting record ID: " + record.id());
    }
}