package com.engine.sink;

import com.engine.transformation.TransformationStrategy;
import com.engine.ingestion.DataRecord;

public interface DataSink {
    void send(DataRecord record);
}