package com.engine.transformation;

import com.engine.ingestion.DataRecord;

public class JsonTransformer implements TransformationStrategy {
    @Override
    public String transform(DataRecord record) {
        return String.format("{\"id\":\"%s\", \"data\":\"%s\"}", record.id(), record.payload());
    }
}