package com.engine.ingestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.SubmissionPublisher;

public class FileIngestor extends SubmissionPublisher<DataRecord> implements AutoCloseable {

    public void startIngestion(String filePath) {
        // We use BufferedReader because it is memory-efficient for large files 
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Convert the raw line into a DataRecord object
                // For now, we assume simple JSONL or CSV format
                DataRecord record = new DataRecord(
                    String.valueOf(System.nanoTime()), 
                    line, 
                    "RAW"
                );

                // submit() blocks if the consumers are too slow (Backpressure) 
                this.submit(record);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            this.close(); // Notify all subscribers that ingestion is done
        }
    }
}