package com.engine.transformation;

import com.engine.ingestion.DataRecord;

public class ProtobufTransformer implements TransformationStrategy {
    @Override
    public String transform(DataRecord record) {
        // Simulates Protobuf binary serialization format
        return "PB_BINARY_DATA{id:" + record.id() + ",payload_hash:" + record.payload().hashCode() + "}";
    }
}