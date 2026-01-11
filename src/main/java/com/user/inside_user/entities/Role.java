package com.user.inside_user.entities;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String name;
    private boolean isDeleted;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;
    private String updatedBy;

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @OneToMany(
            mappedBy = "role"
    )
    private List<User> users;

    public Role(String roleName) {
        this.name = roleName;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }

    public Role() {
    }

    public Role(final Long id, final String name, final List<User> users, boolean isDeleted, String description, Date createdOn, String createdBy, Date updatedOn, String updatedBy) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.isDeleted = isDeleted;
        this.description = description;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.updatedOn = updatedOn;
        this.updatedBy = updatedBy;
    }
}
