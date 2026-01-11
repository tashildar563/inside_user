package com.user.inside_user.response;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public Long getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getRole() {
        return this.role;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public UserResponse() {
    }

    public UserResponse(final Long id, final String firstName, final String lastName, final String email, final String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }
}

