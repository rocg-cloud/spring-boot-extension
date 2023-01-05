package com.livk.mybatis.example.service;

import com.livk.mybatis.example.entity.User;

import java.util.List;

/**
 * <p>
 * UserService
 * </p>
 *
 * @author livk
 * @date 2023/1/5
 */
public interface UserService {
    User getById(Integer id);

    boolean updateById(User user);

    boolean save(User user);

    boolean deleteById(Integer id);

    List<User> list();
}