package com.zss.vsl;

import com.zss.sda.model.User;
import com.zss.sda.util.IdUtil;
import com.zss.sda.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/22 9:46
 * @desc UserService - Test
 */
@Slf4j
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     * createUser(User user);
     */
    @Test
    public void createUserTest() {
        /*User build = User.builder()
                .userCode(IdUtil.getId())
                .username("小明")
                .age(12)
                .desc("小明的个人简介....")
                .enterpriseCode("e83d9915a84d477a989176e224ee3f04")
                .build();*/
        User build = User.builder()
                .userCode(IdUtil.getId())
                .username("小芳")
                .age(11)
                .desc("小芳的个人简介....")
                .enterpriseCode("e83d9915a84d477a989176e224ee3f04")
                .build();
        User user = userService.createUser(build);
        log.info("Create User: [{}]", user);
    }

    @Test
    public void createUserMultiTest() {
        List<User> userList = new ArrayList<>(4);
        for (int i = 1; i <= 12; i++) {
            User build = User.builder()
                    .userCode(IdUtil.getId())
                    .username("员工" + i)
                    .age(10 + i)
                    .desc("员工-" + i + "的个人简介....")
                    .enterpriseCode("e83d9915a84d477a989176e224ee3f04")
                    .build();
            userList.add(build);
        }
        List<User> userMulti = userService.createUserMulti(userList);
        userMulti.forEach(item -> System.out.println("User: " + item));
    }

    @Test
    public void getAllUsersTest() {
        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(item -> System.out.println("User: " + item));
    }

    @Test
    public void getAllUserSortTest() {
        List<User> allUsersSort = userService.getAllUsersSort();
        allUsersSort.forEach(item -> System.out.println("User: " + item));
    }

    @Test
    public void getUserByUserIdTest() {
        Long userId = 1000L;
        User userByUserId = userService.getUserByUserId(userId);
        log.info("User by Id: [{}]", userByUserId);
    }

    @Test
    public void getUserByIdsTest() {
        List<Long> idList = new ArrayList<>(3);
        idList.add(1000L);
        idList.add(1001L);
        idList.add(1002L);
        List<User> userByUserIds = userService.getUserByUserIds(idList);
        userByUserIds.forEach(item -> System.out.println("User: " + item));
    }

    @Test
    public void getUserByUserCodeTest() {
        String userCode = "390b360a99c7467ba0f2f0481f8c8bb3";
        User userByUserCode = userService.getUserByUserCode(userCode);
        log.info("User by UserCode: [{}]", userByUserCode);
    }

}
