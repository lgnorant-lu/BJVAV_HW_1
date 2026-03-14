package com.example.demo.hospital;

import com.example.demo.auth.User;
import com.example.demo.auth.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. 初始化用户
        if (userRepository.count() == 0) {
            userRepository.saveAll(List.of(
                new User("patient", "123456", User.Role.PATIENT),
                new User("admin", "123456", User.Role.ADMIN)
            ));
            System.out.println(">>> 默认用户初始化完成: patient/123456, admin/123456");
        }

        // 2. 初始化科室
        if (departmentRepository.count() == 0) {
            Department cardio = departmentRepository.save(new Department("心内科", "专业诊治各类心脏及血管疾病"));
            Department ortho = departmentRepository.save(new Department("骨科", "专注于骨骼、关节及肌肉损伤的治疗"));
            Department pedia = departmentRepository.save(new Department("儿科", "为婴幼儿及青少年提供全方位健康保障"));

            // 3. 初始化医生
            doctorRepository.saveAll(List.of(
                new Doctor("张医生", cardio.getId(), "主任医师", "冠心病、高血压诊治", 50.0),
                new Doctor("李医生", cardio.getId(), "副主任医师", "心律失常介入治疗", 30.0),
                new Doctor("王医生", ortho.getId(), "主任医师", "脊柱外科、关节置换", 60.0),
                new Doctor("赵医生", pedia.getId(), "主治医师", "小儿呼吸道疾病", 20.0)
            ));
            System.out.println(">>> 医疗业务初始化数据完成");
        }
    }
}
