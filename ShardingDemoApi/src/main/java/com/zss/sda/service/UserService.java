package com.zss.sda.service;

import com.zss.sda.model.User;

import java.util.List;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 13:27
 * @desc 用户服务层接口
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param user 用户实体
     * @return User
     */
    User createUser(User user);

    /**
     * 批量创建用户
     *
     * @param userList 用户实体列表
     * @return userList
     */
    List<User> createUserMulti(List<User> userList);

    /**
     * 获取所有用户
     *
     * @return userList
     */
    List<User> getAllUsers();

    /**
     * 排序获取所有用户
     *
     * @return userList
     */
    List<User> getAllUsersSort();

    /**
     * 通过userId获取用户信息
     *
     * @param userId 用户Id
     * @return User
     */
    User getUserByUserId(Long userId);

    /**
     * 通过UserCode查询用户信息
     *
     * @param userCode 用户编码
     * @return User
     */
    User getUserByUserCode(String userCode);

    /**
     * 批量获取用户数据
     *
     * @param userIds 用户Id列表
     * @return userList
     */
    List<User> getUserByUserIds(List<Long> userIds);
}
