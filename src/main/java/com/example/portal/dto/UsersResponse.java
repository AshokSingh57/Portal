package com.example.portal.dto;

import java.util.List;

public class UsersResponse {
    private boolean success;
    private List<UserDto> users;
    private int count;

    public UsersResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<UserDto> getUsers() { return users; }
    public void setUsers(List<UserDto> users) { this.users = users; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
