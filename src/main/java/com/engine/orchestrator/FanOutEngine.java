package com.engine.orchestrator;

import com.engine.ingestion.DataRecord;
import com.engine.ingestion.FileIngestor;
import com.engine.sink.DataSink;
import com.engine.sink.RestSink;
import com.engine.sink.DatabaseSink;
import com.engine.sink.GrpcSink;
import com.engine.sink.MessageQueueSink;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;


public class FanOutEngine implements Flow.Subscriber<DataRecord> {
    private Flow.Subscription subscription;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final List<DataSink> sinks = List.of(
    new RestSink(), 
    new DatabaseSink(), 
    new GrpcSink(),        // Added
    new MessageQueueSink() // Added
);
    private final java.util.concurrent.atomic.AtomicLong processedCount = new java.util.concurrent.atomic.AtomicLong(0);
    private final java.util.concurrent.atomic.AtomicLong totalProcessed = new java.util.concurrent.atomic.AtomicLong(0);
private final java.util.Map<String, java.util.concurrent.atomic.AtomicLong> successCounters = new java.util.concurrent.ConcurrentHashMap<>();
private final java.util.Map<String, java.util.concurrent.atomic.AtomicLong> failureCounters = new java.util.concurrent.ConcurrentHashMap<>();
private long lastCheckTime = System.currentTimeMillis();
private long lastProcessedCount = 0; 

    private void sendWithRetry(DataSink sink, DataRecord item) {
    // Configure: 3 max attempts, wait 100ms between retries 
    RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(100))
            .build();

    Retry retry = Retry.of(sink.getServiceType() + "-retry", config);

    // This wraps the sink.send(item) call with the retry logic
    Runnable retryableSinkCall = Retry.decorateRunnable(retry, () -> sink.send(item));
    
    try {
        retryableSinkCall.run();
        // If it succeeds (even on the 2nd or 3rd try), count as success
        successCounters.get(sink.getServiceType()).incrementAndGet();
    } catch (Exception e) {
        // Only if it fails ALL 3 times, we count it as a failure [cite: 42]
        failureCounters.get(sink.getServiceType()).incrementAndGet();
        System.err.println("Final failure for " + sink.getServiceType() + " after 3 retries: " + item.id());
    }
}
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1); // Request the first record (Backpressure) [cite: 29]
    }

    @Override
public void onNext(DataRecord item) {
    for (DataSink sink : sinks) {
        // Use Virtual Threads to process each sink's retry logic in parallel [cite: 34]
        executor.submit(() -> sendWithRetry(sink, item));
    }
    totalProcessed.incrementAndGet();
    subscription.request(1); 
}

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Engine Error: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Ingestion Complete. All records processed."); 
        executor.shutdown();
    }
    public FanOutEngine() {
    // Initialize counters for each sink type
    List.of("REST", "DB", "GRPC", "MQ").forEach(type -> {
        successCounters.put(type, new java.util.concurrent.atomic.AtomicLong(0));
        failureCounters.put(type, new java.util.concurrent.atomic.AtomicLong(0));
    });

    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
        long currentTime = System.currentTimeMillis();
        long currentCount = totalProcessed.get();
        
        // Calculate throughput: (records since last check) / (seconds elapsed) [cite: 37]
        double durationSeconds = (currentTime - lastCheckTime) / 1000.0;
        double throughput = (currentCount - lastProcessedCount) / durationSeconds;

        System.out.println("\n--- 5s STATUS UPDATE ---");
        System.out.println("Total Records Processed: " + currentCount);
        System.out.printf("Current Throughput: %.2f records/sec\n", throughput);
        
        successCounters.forEach((sink, count) -> 
            System.out.println(sink + " - Success: " + count.get() + " | Failure: " + failureCounters.get(sink).get()));
        
        // Reset for next window
        lastCheckTime = currentTime;
        lastProcessedCount = currentCount;
    }, 5, 5, java.util.concurrent.TimeUnit.SECONDS);
}
}

