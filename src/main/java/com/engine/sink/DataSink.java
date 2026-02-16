package com.engine.sink;

import com.engine.ingestion.DataRecord;

public interface DataSink {
    void send(DataRecord record);
    String getServiceType(); // Add this line 
}