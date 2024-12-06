package com.gucarsoft.socketbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sensor_data")
public class SensorData {

    @Id
    private String id;

    private long timestamp;
    private int soilHumidity;
    private int lux;
    private double temperature;
    private int co2;
    private int airHumidity;

    // Getters and setters
}

