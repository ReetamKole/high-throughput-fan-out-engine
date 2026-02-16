package com.engine.transformation;

public class TransformerFactory {
    public static TransformationStrategy getStrategy(String sinkType) {
        return switch (sinkType.toUpperCase()) {
            case "REST" -> new JsonTransformer();
            case "MQ" -> new XmlTransformer();
            // We will add Protobuf and Avro strategies here later
            default -> throw new IllegalArgumentException("Unknown sink type: " + sinkType);
        };
    }
}