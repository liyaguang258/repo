package com.example.demo;

import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserService userService;

    @Test
    public void test() throws SQLException {
        System.out.println("asdadf");
        System.out.println(dataSource.getConnection());
    }

    @Test
    public void testUser() {
        List<Map<String, Object>> usersId = userService.getUsersId();
        System.out.println(usersId.size());
        for (Map<String, Object> map : usersId) {
            System.out.println(map);
        }
//        for (Map<String, Object> map : usersId) {
//            System.out.println(map.values());
//        }

    }
}
