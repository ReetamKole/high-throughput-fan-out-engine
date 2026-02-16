package com.engine.transformation;

import com.engine.ingestion.DataRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransformationTest {

    @Test
    void testJsonTransformation() {
        // Arrange
        DataRecord record = new DataRecord("1", "test-payload", "RAW");
        TransformationStrategy strategy = new JsonTransformer();

        // Act
        String result = strategy.transform(record);

        // Assert
        assertTrue(result.contains("\"id\":\"1\""));
        assertTrue(result.contains("\"data\":\"test-payload\""));
    }

    @Test
    void testXmlTransformation() {
        // Arrange
        DataRecord record = new DataRecord("2", "xml-payload", "RAW");
        TransformationStrategy strategy = new XmlTransformer();

        // Act
        String result = strategy.transform(record);

        // Assert
        assertEquals("<record><id>2</id><payload>xml-payload</payload></record>", result);
    }
}