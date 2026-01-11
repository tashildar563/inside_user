package com.user.inside_user.controller;
import com.user.inside_user.Exception.UserException;
import com.user.inside_user.Request.LoginRequest;
import com.user.inside_user.entities.User;
import com.user.inside_user.kafka.Producer;
import com.user.inside_user.response.JwtResponse;
import com.user.inside_user.security.jwtAuth.JwtUtils;
import com.user.inside_user.security.user.UserDetail;
import com.user.inside_user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
/**
 * The AuthenticationController class handles the authentication-related requests.
 * It provides endpoints for user registration, login, update, and deletion.
 * It uses constructor-based dependency injection to inject the necessary services.
 * The class uses the UserService for user-related operations,
 * the AuthenticationManager for authenticating the user during login,
 * the JwtUtils for generating and validating JWT tokens,
 * and the Producer for sending messages to a Kafka topic.
 */
@RestController
@RequestMapping({"/authentication"})
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Producer producer;

    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, Producer producer) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.producer = producer;
    }
    private static final String MESSAGE_KEY = "message";
    private static final String USER_DETAILS_KEY = "userDetails";
    private static final String USER_EMAIL_WITH = "User with email: ";

    @PostMapping({"/register-user"})
    public ResponseEntity<Map<String,Object>> registerUser(@RequestBody User user, HttpServletRequest request) {
        try {
            UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_KEY);
            User loggedInUser = (userService.findByEmail(userDetails.getUsername()));
            this.userService.registerUser(user, loggedInUser.getId());
            JSONObject details;
            details=this.userService.createLoginEventJson(user.getId().toString(), "User Registration", USER_EMAIL_WITH + user.getEmail() + " Registered.");
            producer.sendMessage(details.toString());
            return ResponseEntity.ok(Map.of("id", user.getId(), "firstName", user.getFirstName(), "lastName", user.getLastName(), "email", user.getEmail(), "role", user.getRole().getName(), "status", USER_EMAIL_WITH + user.getEmail() + " Registered."));
        } catch (UserException var3) {
            UserException e = var3;
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE_KEY,e.getMessage()));
        }
    }

    @PostMapping({"/login"})
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = this.jwtUtils.generateJwtTokenForUser(authentication);
        UserDetail userDetails = (UserDetail) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        producer.sendMessage(userService.createLoginEventJson(userDetails.getId().toString(), "Login", USER_EMAIL_WITH + userDetails.getEmail() + "logged in").toString());
        return ResponseEntity.ok(new JwtResponse(userDetails.getId(), userDetails.getEmail(), jwt, roles));
    }

    @PostMapping({"/update"})
    public ResponseEntity<Map<String,Object>> updateUser(@RequestBody @Valid User user, HttpServletRequest request) {
        try {
            UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_KEY);
            User loggedInUser = (userService.findByEmail(userDetails.getUsername()));
            if(loggedInUser.getId().equals(user.getId())){
                throw new UserException("User cannot update his own details");
            }
            Map<String, Object> m = userService.updateUser(user, loggedInUser);
            producer.sendMessage(userService.createLoginEventJson(user.getId().toString(), "Update User", "User details with email: " + user.getEmail() + " Updated").toString());
            return ResponseEntity.ok(m);
        } catch (UsernameNotFoundException var3) {
            UsernameNotFoundException e = var3;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE_KEY, e.getMessage()));
        } catch (Exception var4) {
            Exception e = var4;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(MESSAGE_KEY, "Error Updating user: " + e.getMessage()));
        }
    }

    @DeleteMapping({"/delete/{id}"})
    public ResponseEntity<Map<String,Object>> deleteUser(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_KEY);
            User loggedInUser = (userService.findByEmail(userDetails.getUsername()));
            if(loggedInUser.getId().equals(Long.parseLong(id))){
                throw new UserException("User cannot delete his own details");
            }
            User userDel = this.userService.deleteUser(Long.parseLong(id), loggedInUser);
            producer.sendMessage(userService.createLoginEventJson(id, "Delete User", USER_EMAIL_WITH + userDel.getEmail() + " deleted").toString());
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, USER_EMAIL_WITH + userDel.getEmail() + " deleted.", "email", userDel.getEmail(), "id", userDel.getId()));
        } catch (UsernameNotFoundException var3) {
            UsernameNotFoundException e = var3;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE_KEY,e.getMessage()));
        } catch (Exception var4) {
            Exception e = var4;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(MESSAGE_KEY,"Error deleting user: " + e.getMessage()));
        }
    }
}
