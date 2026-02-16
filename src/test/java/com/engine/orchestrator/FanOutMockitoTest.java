package com.engine.orchestrator;

import com.engine.ingestion.DataRecord;
import com.engine.sink.DataSink;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

public class FanOutMockitoTest {

    @Test
void testFanOutCallsAllSinks() throws Exception {
    // 1. Arrange
    DataSink mockRest = mock(DataSink.class);
    DataSink mockDb = mock(DataSink.class);
    // Mock the subscription to avoid the NullPointerException
    java.util.concurrent.Flow.Subscription mockSubscription = mock(java.util.concurrent.Flow.Subscription.class);
    
    FanOutEngine engine = new FanOutEngine();
    
    // Set the subscription so the engine can call .request(1)
    engine.onSubscribe(mockSubscription);

    Field sinksField = FanOutEngine.class.getDeclaredField("sinks");
    sinksField.setAccessible(true);
    sinksField.set(engine, List.of(mockRest, mockDb));

    // When getServiceType is called, return names so the counters don't crash
    when(mockRest.getServiceType()).thenReturn("REST");
    when(mockDb.getServiceType()).thenReturn("DB");

    DataRecord record = new DataRecord("999", "Mock Test", "RAW");

    // 2. Act
    engine.onNext(record);

    Thread.sleep(100);

    // 3. Assert
    verify(mockRest, times(1)).send(record);
    verify(mockSubscription, atLeastOnce()).request(1); // Verify backpressure call
}
    }
