package com.example.demo.hospital;

import com.example.demo.auth.RequireRole;
import com.example.demo.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital")
@CrossOrigin(origins = "*")
public class HospitalController {

    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public HospitalController(DepartmentRepository departmentRepository,
                              DoctorRepository doctorRepository,
                              AppointmentRepository appointmentRepository) {
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // --- 科室接口 ---
    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @PostMapping("/departments")
    @RequireRole(User.Role.ADMIN)
    public Department createDepartment(@RequestBody Department department) {
        return departmentRepository.save(department);
    }

    // --- 医生接口 ---
    @GetMapping("/doctors")
    public List<Doctor> getDoctors(@RequestParam(required = false) String departmentId) {
        if (departmentId != null) {
            return doctorRepository.findByDepartmentId(departmentId);
        }
        return doctorRepository.findAll();
    }

    @PostMapping("/doctors")
    @RequireRole(User.Role.ADMIN)
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // --- 预约接口 ---
    @PostMapping("/appointments")
    @RequireRole(User.Role.PATIENT)
    public Appointment createAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        appointment.setPatientId(userId);
        appointment.setStatus(Appointment.Status.PENDING);
        return appointmentRepository.save(appointment);
    }

    @GetMapping("/appointments/my")
    @RequireRole(User.Role.PATIENT)
    public List<Appointment> getMyAppointments(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return appointmentRepository.findByPatientId(userId);
    }
}
