package com.user.inside_user.service;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.user.inside_user.Exception.RoleAlreadyExistException;
import com.user.inside_user.entities.Role;
import com.user.inside_user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleService() {
    }

    public List<Role> getRoles() {
        return this.roleRepository.findAll();
    }

    public Role createRole(Role theRole, String createdBy) {
        String roleName = "ROLE_" + theRole.getName().toUpperCase();
        Role role = new Role(roleName);
        role.setCreatedBy(createdBy);
        role.setCreatedOn(new Date());
        role.setDeleted(false);
        role.setUpdatedBy(createdBy);
        role.setUpdatedOn(new Date());
        if (this.roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistException(theRole.getName() + " role already exists");
        } else {
            return (Role)this.roleRepository.save(role);
        }
    }

    public void deleteRole(Long roleId) {
        Optional<Role> role = this.roleRepository.findById(roleId);
        if (role.isPresent()) {
            this.roleRepository.deleteById(roleId);
        }

    }
    public Role finOneById(Long id) {
        return roleRepository.findById(id).get();
    }

    public Role findOneByName(String roleCode) {
        return roleRepository.findByName(roleCode).get();
    }

    public void deleteRoleByCode(String roleCode,String updatedBy) {
        Role role = roleRepository.findByName(roleCode).get();
        role.setDeleted(true);
        role.setUpdatedBy(updatedBy);
        role.setUpdatedOn(new Date());
        roleRepository.save(role);
    }
}

