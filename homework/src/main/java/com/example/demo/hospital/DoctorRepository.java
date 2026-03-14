package com.example.demo.hospital;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    List<Doctor> findByDepartmentId(String departmentId);
}
