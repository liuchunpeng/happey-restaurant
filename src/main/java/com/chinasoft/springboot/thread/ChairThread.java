package com.chinasoft.springboot.thread;

import com.chinasoft.springboot.entities.Chair;
import com.chinasoft.springboot.entities.Customer;
import com.chinasoft.springboot.entities.Shop;
import com.chinasoft.springboot.entities.Table;
import lombok.SneakyThrows;

import java.util.List;

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

//            synchronized (customers){
//                System.out.println("chair"+customers);
//                while (customers.size()<=0){
//                    try {
//                        customers.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (customers.size()>0){
//                    System.out.println(customers.get(0).getCusName() + "ruzuo");
//                    customers.remove(0);
//                }
//            }
        }
    }
}
