package com.engine.sink;

import com.engine.ingestion.DataRecord;
import com.engine.transformation.ProtobufTransformer;
import com.engine.transformation.TransformationStrategy;

public class GrpcSink implements DataSink {
    private final TransformationStrategy transformer = new ProtobufTransformer();

@Override
public void send(DataRecord record) {
    String payload = transformer.transform(record); // Source -> Protobuf
    System.out.println("[gRPC Sink] Sending binary: " + payload);
}
    
    @Override
    public String getServiceType() {
        return "GRPC";
    }
}