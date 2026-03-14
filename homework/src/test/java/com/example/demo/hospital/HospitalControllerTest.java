package com.example.demo.hospital;

import com.example.demo.auth.JwtUtils;
import com.example.demo.auth.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testGetDepartmentsWithoutToken() throws Exception {
        // 未携带 Token 访问受保护接口（如果配置了拦截器）
        // 注意：WebConfig 中排除了 login/register，其他 /api/** 都会被拦截
        mockMvc.perform(get("/api/hospital/departments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetDepartmentsWithToken() throws Exception {
        User user = new User("patient", "123456", User.Role.PATIENT);
        user.setId("patient-id");
        String token = jwtUtils.generateToken(user);

        mockMvc.perform(get("/api/hospital/departments")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
