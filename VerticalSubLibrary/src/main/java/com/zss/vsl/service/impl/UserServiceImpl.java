package com.zss.vsl.service.impl;

import com.zss.sda.model.User;
import com.zss.vsl.repository.UserRepository;
import com.zss.sda.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 13:52
 * @desc User服务层方法实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        try {
            System.out.println("Before Save: " + user);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("创建用户失败: [{}]", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<User> createUserMulti(List<User> userList) {
        try {
            return userRepository.saveAll(userList);
        } catch (Exception e) {
            log.error("批量创建用户失败: [{}]", e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllUsersSort() {
        return userRepository.findAllByOrderByIdDesc();
    }

    @Override
    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUserByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode).orElse(null);
    }

    @Override
    public List<User> getUserByUserIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }
}
