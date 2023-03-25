package com.crowstudy.springdataredisstudy;

import com.crowstudy.springdataredisstudy.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class SpringDataRedisStudyApplicationTests {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testString() {
        // 写入一条String数据
        redisTemplate.opsForValue().set("name", "java");
        // 获取string数据
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);
    }

    @Test
    void testSaveUser(){
        redisTemplate.opsForValue().set("user:100",new User("gzp",22));
        User user = (User)redisTemplate.opsForValue().get("user:100");
        System.out.println(user);
    }
}
