package com.user.inside_user.response;


public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private String status;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public RoleResponse() {
    }

    public RoleResponse(final Long id, final String name, final String description, final String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }
}

