package com.chinasoft.springboot.entities;

import lombok.Data;

/**
 * @author 15075
 * @date 2021/4/6 09:19:05
 * @discription
 */
@Data
public class Food {
    /**
     * name
     */
    private String foodName;
    /**
     * id
     */
    private Integer foodId;

    /**
     * no
     */
    private Integer foodNo;
    /**
     * 点了该食物的顾客
     */
    private Customer byCustomer;
}
