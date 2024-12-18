package ru.skillbox.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.authservice.domain.User;
import ru.skillbox.authservice.repository.UserRepository;
import ru.skillbox.authservice.security.SecurityConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@WithMockUser(username = "Sidorov")
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private User newUser;
    private List<User> users;

    @Configuration
    @ComponentScan(basePackageClasses = {UserController.class, SecurityConfiguration.class})
    public static class TestConf {
    }

    @BeforeEach
    public void setUp() {
        Mockito.when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0) + "_some_fake_encoding");

        user = new User("Petrov", passwordEncoder.encode("superpass"));
        newUser = new User("Ivanov", passwordEncoder.encode("superpass99"));
        users = Collections.singletonList(user);
    }

    @Test
    public void getUser() throws Exception {
        Mockito.when(userRepository.findByName(user.getName())).thenReturn(Optional.of(user));

        mvc.perform(get("/user/Petrov"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(user.getName())));
    }

    @Test
    public void getAllUsers() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(users);

        mvc.perform(get("/user/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", equalTo(user.getName())));
    }

    @Test
    public void createUser() throws Exception {
        Mockito.when(userRepository.save(any(User.class))).thenReturn(newUser);

        mvc.perform(
                        post("/user/signup")
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Ivanov\",\"password\":\"superpass99\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())  // Посмотрите JSON-ответ
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ivanov"));  // Проверка поля "name"
    }
}