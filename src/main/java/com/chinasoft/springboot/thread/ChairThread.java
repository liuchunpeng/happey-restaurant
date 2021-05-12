package com.chinasoft.springboot.thread;

import com.chinasoft.springboot.entities.Shop;
import lombok.SneakyThrows;

/**
 * @author 15075
 * @date 2021/4/8 09:15:38
 * @discription
 */
public class ChairThread implements Runnable{
    Shop shop;
    public ChairThread(Shop shop){
        this.shop=shop;
    }
    @SneakyThrows
    @Override
    public void run() {
        while(true){
            shop.paiChair();
        }
    }
}
