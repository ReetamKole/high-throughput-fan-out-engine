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
}