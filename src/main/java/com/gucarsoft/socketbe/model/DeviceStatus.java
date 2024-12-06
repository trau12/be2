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
@Document(collection = "device_status")
public class DeviceStatus {

    @Id
    private String id;

    private int quatHut;
    private int quatThoi;
    private int soNongLanh;
    private int mayBom;
    private int rem;

    // Getters and setters
}