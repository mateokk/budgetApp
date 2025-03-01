package com.example.budget.controllers;

import com.example.budget.dto.UserLoginDTO;
import com.example.budget.dto.UserRegisterDTO;
import com.example.budget.model.User;
import com.example.budget.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldRegisterUser() throws Exception {
        UserRegisterDTO userDTO = new UserRegisterDTO("testUsername", "test@email", "testPassword");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userDTO))
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.username").value("testUsername")
        ).andExpect(jsonPath("$.email").value("test@email")
        );
        User user = userRepository.findByUsername("testUsername").orElseThrow();
        assertEquals("test@email", user.getEmail());
        assertNotEquals("testPassword", user.getPassword());
    }

    @Test
    public void shouldLoginUser() throws Exception {
        registerNewUser();
        UserLoginDTO userLoginDTO = new UserLoginDTO("testUser", "testPassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginDTO))
        ).andExpect(status().isOk()
        );
    }
    @Test
    public void shouldReturnUnauthorizedWhenLoginFails() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("testUser", "testPassword");
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void shouldGetCurrentUser() throws Exception {
        registerNewUser();
        mockMvc.perform(get("/auth/me")
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.username").value("testUser")
        ).andExpect(jsonPath("$.email").value("test@email"));
    }
    @Test
    public void shouldReturnUnauthorizedWhenAccessingMeWithoutAuth() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }


    public void registerNewUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@email");
        user.setPassword(new BCryptPasswordEncoder().encode("testPassword"));
        userRepository.save(user);
    }

}
