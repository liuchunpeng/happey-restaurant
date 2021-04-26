package com.chinasoft.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 15075
 */
@SpringBootApplication

@ComponentScan(basePackages = {"com"})
@MapperScan("com.chinasoft.springboot.dao")
public class HappyRestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappyRestaurantApplication.class, args);
    }

}
