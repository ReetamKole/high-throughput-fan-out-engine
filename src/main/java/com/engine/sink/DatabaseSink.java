package com.engine.sink;

import com.engine.ingestion.DataRecord;
import com.engine.transformation.TransformationStrategy;
import com.engine.transformation.AvroTransformer;
// Note: In a full impl, you'd create an AvroTransformer here
import com.engine.transformation.JsonTransformer; 

public class DatabaseSink implements DataSink {
    private final TransformationStrategy transformer = new AvroTransformer();

@Override
public void send(DataRecord record) {
    String payload = transformer.transform(record); // Source -> Avro/CQL Map
    System.out.println("[DB Sink] Upserting: " + payload);
}
    @Override
public String getServiceType() {
    return "DB";
}
    
}