package com.engine;

import com.engine.ingestion.FileIngestor;
import com.engine.orchestrator.FanOutEngine;
import java.io.File;
import java.io.PrintWriter;

public class App {
    public static void main(String[] args) throws Exception {
        // Create a dummy data file to process
        File dataDir = new File("data");
        if (!dataDir.exists()) dataDir.mkdir();
        File inputFile = new File("data/input.jsonl");
        
        try (PrintWriter writer = new PrintWriter(inputFile)) {
            for (int i = 1; i <= 50; i++) {
                writer.println("{\"id\":" + i + ", \"msg\":\"Hello World\"}");
            }
        }

        System.out.println("--- Starting Fan-Out Engine ---");
        FanOutEngine engine = new FanOutEngine();
        FileIngestor ingestor = new FileIngestor();

        ingestor.subscribe(engine);
        ingestor.startIngestion(inputFile.getAbsolutePath());
    }
}