//package com.BankApplication.bankapplication;
//
//import com.BankApplication.bankapplication.dto.BankResponse;
//import com.BankApplication.bankapplication.dto.UserRequest;
//import com.BankApplication.bankapplication.entity.User;
//import com.BankApplication.bankapplication.enums.Gender;
//import com.BankApplication.bankapplication.repository.UserRepository;
//import com.BankApplication.bankapplication.service.impl.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.verify;
//
//
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//public class UserControllerIntegrationTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @BeforeEach
//    public void setup() {
//        // Delete all records from the necessary tables in the H2 database
//        userRepository.deleteAll();
//    }
//
//    @Test
//    public void testCreateAccount() {
//        UserRequest userRequest = new UserRequest();
//        userRequest.setFirstName("John");
//        userRequest.setLastName("Doe");
//        userRequest.setEmail("kent121212@gmail.com");
//        userRequest.setPassword("password");
//        userRequest.setMiddleName("Abraham");
//        userRequest.setGender(Gender.MALE);
//        userRequest.setAddress("Tirana ,Albania");
//        userRequest.setStateOfOrigin("Albania");
//        userRequest.setPhoneNumber("0692020201");
//        userRequest.setAlternativePhone("084457565");
//
//        BankResponse bankResponse = BankResponse.builder()
//                .responseCode("002")
//                .responseMessage("Account has been successfully created.")
//                // Add other response attributes
//                .build();
//
//
//        BankResponse actualResponse = userService.createAccount(userRequest);
//
//        // Assert the response
//        assertEquals(bankResponse.getResponseCode(), actualResponse.getResponseCode());
//        assertEquals(bankResponse.getResponseMessage(), actualResponse.getResponseMessage());
//        // Add assertions for other response attributes
//
//        // Verify the userService.createAccount() method is called with the correct argument
//        verify(userService).createAccount(userRequest);
//
//
//    }
//
//
//    @Test
//    public void testSaveUser() {
//        User user = new User();
//        user.setFirstName("John");
//        user.setLastName("Doe");
//        user.setEmail("john.doe@example.com");
//
//        User savedUser = userRepository.save(user);
//
//        assertNotNull(savedUser.getId());
//        assertEquals("John", savedUser.getFirstName());
//        assertEquals("Doe", savedUser.getLastName());
//        assertEquals("john.doe@example.com", savedUser.getEmail());
//    }
//
//    // Add more integration tests for other controller methods
//}