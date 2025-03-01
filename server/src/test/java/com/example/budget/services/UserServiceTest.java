package com.example.budget.services;

import com.example.budget.model.User;
import com.example.budget.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldRegisterUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        User savedUser = userService.registerUser(user);

        assertEquals(user, savedUser);
        assertEquals("testUser", savedUser.getUsername());
        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldReturnRuntimeExceptionWhenRegisterWithExistingUsername() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> userService.registerUser(user));
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, never()).save(user);
    }

    @Test
    public void shouldFindUserByUsername() {
        User user = new User();
        String username = "testUser";
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(username);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void shouldVerifyUserSuccessfully() {
        String username = "testUser";
        String password = "password";
        String token = "token";
        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(username)).thenReturn(token);

        String result = userService.verify(username, password);

        assertEquals(token, result);
        assertNotNull(result);
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(username);
    }

    @Test
    public void shouldThrowExceptionWhenAuthenticationFails() {
        String username = "testUser";
        String password = "wrongPassword";
        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        assertThrows(BadCredentialsException.class, () -> userService.verify(username, password));
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(username);
    }


}