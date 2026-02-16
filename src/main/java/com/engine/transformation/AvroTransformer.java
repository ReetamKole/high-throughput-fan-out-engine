package com.engine.transformation;

import com.engine.ingestion.DataRecord;

public class AvroTransformer implements TransformationStrategy {
    @Override
    public String transform(DataRecord record) {
        // Simulates an Avro/CQL Map format for Wide-column DBs
        return "AVRO_RECORD{id:\"" + record.id() + "\", data_map:{\"val\":\"" + record.payload() + "\"}}";
    }
}