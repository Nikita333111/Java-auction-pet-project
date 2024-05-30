package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.model.AuctionUser;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    AuctionUser findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
