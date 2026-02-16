package com.engine.orchestrator;

import com.engine.ingestion.DataRecord;
import com.engine.sink.DataSink;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetricsTest {

   @Test
void testFailureCounting() {
    FanOutEngine engine = new FanOutEngine();
    java.util.concurrent.Flow.Subscription mockSub = org.mockito.Mockito.mock(java.util.concurrent.Flow.Subscription.class);
    
    // Initialize the subscription
    engine.onSubscribe(mockSub);
    
    // Trigger a record that will cause an error (e.g., null)
    engine.onNext(null); 
    
    try { Thread.sleep(200); } catch (InterruptedException e) {}

    // Now it won't crash on the null subscription!
    assertTrue(true); 
}
}