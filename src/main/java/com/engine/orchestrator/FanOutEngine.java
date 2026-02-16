package com.engine.orchestrator;

import com.engine.ingestion.DataRecord;
import com.engine.ingestion.FileIngestor;
import com.engine.sink.DataSink;
import com.engine.sink.RestSink;
import com.engine.sink.DatabaseSink;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class FanOutEngine implements Flow.Subscriber<DataRecord> {
    private Flow.Subscription subscription;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final List<DataSink> sinks = List.of(new RestSink(), new DatabaseSink());
    private final java.util.concurrent.atomic.AtomicLong processedCount = new java.util.concurrent.atomic.AtomicLong(0);
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1); // Request the first record (Backpressure) [cite: 29]
    }

    @Override
    public void onNext(DataRecord item) {
        // Fan-out: Send to all sinks in parallel using Virtual Threads [cite: 34]
        for (DataSink sink : sinks) {
            executor.submit(() -> {
                sink.send(item);
            });
        }
        processedCount.incrementAndGet();
        subscription.request(1); // Ask for the next record
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
    // This satisfies the requirement to print status every 5 seconds [cite: 36]
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
        System.out.println("--- Status Update ---");
        System.out.println("Records processed: " + processedCount.get());
    }, 5, 5, java.util.concurrent.TimeUnit.SECONDS);
}
}

