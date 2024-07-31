package com.zss.hsl.repository;

import com.zss.sda.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 14:15
 * @desc User持久化
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 查询所有用户
     *
     * @return userList
     */
    List<User> findAllByOrderByIdDesc();

    /**
     * 通过用户编码获取用户
     *
     * @param userCode 用户编码
     * @return User
     */
    Optional<User> findByUserCode(String userCode);
}
