package com.chinasoft.springboot.thread;

import com.chinasoft.springboot.entities.Chef;
import com.chinasoft.springboot.entities.Shop;

import java.util.List;

/**
 * @author 15075
 * @date 2021/4/6 16:27:15
 * @discription
 */

public class ChefThread implements Runnable{
    private Shop shop;

    public ChefThread(Shop shop){
        this.shop = shop;
    }

    @Override
    public void run() {
        while (true){
            shop.zuoCai();
        }

    }
}