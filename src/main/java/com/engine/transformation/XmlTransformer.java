package com.engine.transformation;

import com.engine.ingestion.DataRecord;

public class XmlTransformer implements TransformationStrategy {
    @Override
    public String transform(DataRecord record) {
        return String.format("<record><id>%s</id><payload>%s</payload></record>", record.id(), record.payload());
    }
}