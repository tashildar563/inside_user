package com.user.inside_user.controller;

import com.user.inside_user.Exception.RoleAlreadyExistException;
import com.user.inside_user.entities.Role;
import com.user.inside_user.entities.User;
import com.user.inside_user.kafka.Producer;
import com.user.inside_user.response.RoleResponse;
import com.user.inside_user.service.RoleService;
import com.user.inside_user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The RoleController class handles the role-related requests.
 * It provides endpoints for creating, retrieving, and deleting roles.
 * It uses the RoleService for role-related operations,
 * the UserService for user-related operations,
 * and the Producer for sending messages to a Kafka topic.
 */
@RestController
@RequestMapping({"/roles"})
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;
    private final Producer producer;

    @Autowired
    public RoleController(RoleService roleService, UserService userService, Producer producer) {
        this.roleService = roleService;
        this.userService = userService;
        this.producer = producer;
    }

    @GetMapping({"/all-roles"})
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<Role> roles = this.roleService.getRoles();
        List<RoleResponse> roleResponses = new ArrayList<>();

        for (Role role : roles) {
            if (role.isDeleted()) {
                continue;
            }
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());
            roleResponses.add(roleResponse);
        }

        return new ResponseEntity<>(roleResponses, HttpStatus.FOUND);
    }

    @PostMapping({"/create-new-role"})
    public ResponseEntity<RoleResponse> createRole(@RequestBody Role theRole, HttpServletRequest request) {
        try {
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            User user = (userService.findByEmail(userDetails.getUsername()));
            theRole = this.roleService.createRole(theRole, String.valueOf(user.getId()));
            producer.sendMessage(userService.createLoginEventJson(theRole.getId().toString(), "Role Creation", "Role with name: " + theRole.getName() + "Created.").toString());
            return ResponseEntity.ok(new RoleResponse(theRole.getId(), theRole.getName(), theRole.getDescription(), "New role created successfully!"));
        } catch (RoleAlreadyExistException var3) {
            RoleAlreadyExistException re = var3;
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RoleResponse(null, theRole.getName(), theRole.getDescription(), re.getMessage()));
        }
    }

    @DeleteMapping({"/delete/{roleCode}"})
    public ResponseEntity<RoleResponse> deleteRole(@PathVariable("roleCode") String roleCode, HttpServletRequest request) {
        try {
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            User user = (userService.findByEmail(userDetails.getUsername()));
            roleCode = "ROLE_" + roleCode;
            Role role = this.roleService.findOneByName(roleCode);
            this.roleService.deleteRoleByCode(roleCode, String.valueOf(user.getId()));
            producer.sendMessage(userService.createLoginEventJson(role.getId().toString(), "Role Deletion", "Role with code: " + role.getName() + "Deleted.").toString());
            return ResponseEntity.ok(new RoleResponse(role.getId(), role.getName(), role.getDescription(), "Role deleted successfully!"));
        } catch (RoleAlreadyExistException var3) {
            RoleAlreadyExistException re = var3;
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RoleResponse(null, roleCode, roleCode, re.getMessage()));
        }
    }
}

