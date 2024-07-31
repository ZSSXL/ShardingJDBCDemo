package com.zss.rws;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/22 9:38
 * @desc 测试基类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReadWriteSeparationApplication.class)
public class BaseTest {

    @Before
    public void before() {
        System.out.println("======================= Starting Test =======================");
    }

    @After
    public void after() {
        System.out.println("======================== Ending Test ========================");
    }
}
