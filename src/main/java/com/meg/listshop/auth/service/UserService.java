package com.meg.listshop.auth.service;

import com.meg.listshop.auth.data.entity.UserEntity;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface UserService {

    UserEntity getUserById(Long userId);

    UserEntity getUserByUserEmail(String userEmail);

    UserEntity save(UserEntity user);

    UserEntity createUser(String username, String decodedEmail, String decodedPassword);
}