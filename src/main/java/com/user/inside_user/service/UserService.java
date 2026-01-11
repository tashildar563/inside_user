package com.user.inside_user.service;

import com.user.inside_user.Exception.UserException;
import com.user.inside_user.entities.Role;
import com.user.inside_user.entities.User;
import com.user.inside_user.repository.RoleRepository;
import com.user.inside_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public static JSONObject createLoginEventJson(String userId, String event, String message) {
        JSONObject member = new JSONObject();
        JSONObject data = new JSONObject();
        member.put("created_by", userId);
        member.put("created_on", String.valueOf(System.currentTimeMillis()));
        member.put("event", event);
        data.put("event", event);
        data.put("id", userId);
        data.put("message", message);
        member.put("data", data.toString());
        return member;
    }

    public User registerUser(User user, Long id) {
        String enterEmailIdString = user.getEmail();
        Optional<User> existingUser = this.userRepository.findByEmail(enterEmailIdString);
        if (existingUser.isPresent()) {
            throw new UserException(user.getEmail() + " already exists");
        } else {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            Role userRole = this.roleRepository.findByName(user.getRole().getName()).get();
            if (userRole == null || userRole.isDeleted()) {
                throw new UserException("Role not found");
            }
            user.setRole(userRole);
            user.setDeleted(false);
            user.setCreatedBy(String.valueOf(id));
            user.setUpdatedBy(String.valueOf(id));
            user.setCreatedOn(new Date());
            user.setUpdatedOn(new Date());
            return this.userRepository.save(user);
        }
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Transactional
    public User deleteUser(Long id, User loggedInUser) {
        Optional<User> theUser = this.userRepository.findById(id);
        if (theUser.isPresent() && !theUser.get().isDeleted()) {
            User u = theUser.get();
            u.setDeleted(true);
            u.setUpdatedOn(new Date());
            u.setUpdatedBy(String.valueOf(loggedInUser.getId()));
            return this.userRepository.save(u);
        } else {
            throw new UserException("User not found");
        }
    }

    public User getUser(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> {
            return new UsernameNotFoundException("User not found");
        });
    }

    public User findOneById(Long id) {
        return userRepository.findById(id).get();
    }

    public User findByEmail(String username) {
        return userRepository.findByEmail(username).get();
    }

    public Map<String, Object> updateUser(User user, User loggedInUser) {
        User userValid = findOneById(user.getId());
        if (userValid == null || userValid.isDeleted()) {
            throw new UserException(user.getEmail() +" "+ HttpStatus.NOT_FOUND);
        }
        if(findByEmail(user.getEmail())!=null && user.getId() != findByEmail(user.getEmail()).getId()){
            throw new UserException(user.getEmail() +": Duplicate Email "+ HttpStatus.NOT_ACCEPTABLE);
        }
        userValid.setFirstName(user.getFirstName());
        userValid.setLastName(user.getLastName());
        userValid.setEmail(user.getEmail());
        Role role = roleRepository.findByName(user.getRole().getName()).get();
        userValid.setRole(role);
        userValid.setUpdatedBy(String.valueOf(loggedInUser.getId()));
        userValid.setUpdatedOn(new Date());
        this.userRepository.save(userValid);
        return Map.of("message", "User with email: " + user.getEmail() + " updated.", "email", user.getEmail(), "id", user.getId());
    }
}

