package com.gucarsoft.socketbe.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gucarsoft.socketbe.model.DeviceStatus;
import com.gucarsoft.socketbe.model.SensorData;
import com.gucarsoft.socketbe.repo.DeviceStatusRepository;
import com.gucarsoft.socketbe.repo.SensorDataRepository;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private DeviceStatusRepository deviceStatusRepository;

    @Autowired
    private InfluxDBClient influxDBClient;

    @MessageMapping("/chat")
    public void processMessage(@Payload String message) {
        try {
            // Parse the received message
            JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
            String roomName = jsonObject.get("roomName") != null ? jsonObject.get("roomName").getAsString() : "defaultRoom";
            String messageType = jsonObject.get("messageType") != null ? jsonObject.get("messageType").getAsString() : "UNKNOWN";

            log.info("Room name: " + roomName);
            log.info("Received message: " + message);

            if ("SENSOR".equals(messageType)) {
                // Parse sensor data
                SensorData sensorData = new Gson().fromJson(jsonObject.get("message").getAsString(), SensorData.class);

                // Save sensor data to MongoDB
                sensorDataRepository.save(sensorData);

                // Save sensor data to InfluxDB
                Point point = Point.measurement("sensor_data")
                        .addTag("room", roomName)
                        .addField("soilHumidity", sensorData.getSoilHumidity())
                        .addField("lux", sensorData.getLux())
                        .addField("temperature", sensorData.getTemperature())
                        .addField("co2", sensorData.getCo2())
                        .addField("airHumidity", sensorData.getAirHumidity())
                        .time(sensorData.getTimestamp(), WritePrecision.MS);
                influxDBClient.getWriteApi().writePoint(point);

                // Send sensor data to frontend via WebSocket
                messagingTemplate.convertAndSend("/topic/chat/" + roomName, message);
                JsonObject messageDetails = jsonObject.getAsJsonObject("message");
                log.info("Message Details: " + messageDetails.toString());

            } else if ("DEVICE".equals(messageType)) {
                // Parse device status
                DeviceStatus deviceStatus = new Gson().fromJson(jsonObject.get("message").getAsString(), DeviceStatus.class);

                // Save device status to MongoDB
                deviceStatusRepository.save(deviceStatus);

                // Send device status to frontend via WebSocket
                messagingTemplate.convertAndSend("/topic/chat/" + roomName, message);
            } else {
                log.warn("Unknown message type: " + messageType);
            }
        } catch (Exception e) {
            log.error("Error processing message: " + message, e);
        }
    }
}

