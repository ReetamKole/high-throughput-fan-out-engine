package com.engine.transformation;

import com.engine.ingestion.DataRecord;

public interface TransformationStrategy {
    // This converts our DataRecord into the specific String format needed by the Sink
    String transform(DataRecord record);
}