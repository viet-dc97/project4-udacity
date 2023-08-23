package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Objects;
import java.util.Optional;

public class UserControllerTest {
  UserRepository userRepository;
  CartRepository cartRepository;
  BCryptPasswordEncoder bCryptPasswordEncoder;
  UserController userController;

  @Before
  public void setUp() {
    userRepository = mock(UserRepository.class);
    cartRepository = mock(CartRepository.class);
    bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
    User user = new User();
    user.setId(1);
    user.setUsername("VietDC1");
    user.setPassword("1234567");
    user.setCart(new Cart());
    when(userRepository.findByUsername("VietDC1")).thenReturn(user);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bCryptPasswordEncoder.encode("1234567")).thenReturn("1234567Encode");
  }

  @Test
  public void findById() {
    ResponseEntity<User> response = userController.findById(1L);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, Objects.requireNonNull(response.getBody()).getId());
  }

  @Test
  public void findByIdNotFound() {
    ResponseEntity<User> response = userController.findById(2L);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void findByUserName() {
    ResponseEntity<User> response = userController.findByUserName("VietDC1");
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("VietDC1", Objects.requireNonNull(response.getBody()).getUsername());
  }

  @Test
  public void findByUserNameNotFound() {
    ResponseEntity<User> response = userController.findByUserName("abc");
    assertEquals(404, response.getStatusCodeValue());
  }


  @Test
  public void createUser() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("VietDC1");
    request.setPassword("1234567");
    request.setConfirmPassword("1234567");
    ResponseEntity<User> response = userController.createUser(request);
    assertEquals(200, response.getStatusCodeValue());
    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("VietDC1", user.getUsername());
    assertEquals("1234567Encode", user.getPassword());
  }

  @Test
  public void createUserBadRequest() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("VietDC1");
    request.setPassword("admin");
    request.setConfirmPassword("admin");
    ResponseEntity<User> response = userController.createUser(request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void create_user_password_confirm_mismatch() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("VietDC1");
    request.setPassword("admin");
    request.setConfirmPassword("admin");
    ResponseEntity<User> response = userController.createUser(request);
    assertEquals(400, response.getStatusCodeValue());
  }
}