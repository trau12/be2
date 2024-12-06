package com.gucarsoft.socketbe.repo;

import com.gucarsoft.socketbe.model.DeviceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DeviceStatusRepository extends MongoRepository<DeviceStatus, String> {
}
