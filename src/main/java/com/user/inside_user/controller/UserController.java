package com.user.inside_user.controller;
import com.user.inside_user.Request.JournalRequest;
import com.user.inside_user.entities.User;
import com.user.inside_user.response.UserResponse;
import com.user.inside_user.service.RestService;
import com.user.inside_user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * The UserController class handles the user-related requests.
 * It provides endpoints for retrieving all users, retrieving a user by email, and getting user's journal entries.
 * It uses the UserService for user-related operations and the RestService for making requests to other services.
 * The class uses constructor-based dependency injection to inject the necessary services.
 */
@RestController
@RequestMapping({"/users"})
public class UserController {
    private final UserService userService;
    private final RestService restService;

    @Autowired
    public UserController(UserService userService, RestService restService) {
        this.userService = userService;
        this.restService = restService;
    }

    @GetMapping({"/all"})
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<User> users = this.userService.getUsers();
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : users) {
            if (user.isDeleted()) {
                continue;
            }
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setEmail(user.getEmail());
            userResponse.setRole(user.getRole().getName());
            userResponses.add(userResponse);
        }

        return new ResponseEntity<>(userResponses, HttpStatus.FOUND);
    }

    @GetMapping({"{email}"})
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable("email") String email) {
        try {
            User theUser = this.userService.getUser(email);
            Map<String, Object> user = Map.of("id", theUser.getId(), "firstName", theUser.getFirstName(), "lastName", theUser.getLastName(), "email", theUser.getEmail(), "role", theUser.getRole().getName());
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException var3) {
            UsernameNotFoundException e = var3;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception var4) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error fetching user"));
        }
    }

    @GetMapping({"/journal"})
    public ResponseEntity<String> getUsersJournalEntries(@RequestBody @Valid JournalRequest request) {
        String users = this.restService.getDataFromOtherService(request.getEvent());
        return new ResponseEntity<>(users, HttpStatus.FOUND);
    }
}
