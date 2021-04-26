package com.chinasoft.springboot.thread;

import com.chinasoft.springboot.entities.Chair;
import com.chinasoft.springboot.entities.Customer;
import com.chinasoft.springboot.entities.Shop;
import com.chinasoft.springboot.entities.Table;

import java.util.List;

/**
 * @author 15075
 * @date 2021/4/6 16:28:42
 * @discription
 */

public class MakeCustomerThread implements Runnable{
    private Shop shop;

    public MakeCustomerThread (Shop shop){
        this.shop = shop;
    }

    @Override
    public void run() {
        //招揽顾客
        while (true){
            shop.addCustomer();
        }
    }
}