package com.itoffice.ticketsystem;

import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User createTestUser(String username, Role role) {
        User user = User.builder()
                .username(username)
                .email(username + "@test.com")
                .password(passwordEncoder.encode("Test@1234"))
                .role(role)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}
