package com.chinasoft.springboot.controller;

import com.chinasoft.springboot.entities.*;
import com.chinasoft.springboot.thread.ChairThread;
import com.chinasoft.springboot.thread.ChefThread;
import com.chinasoft.springboot.thread.MakeCustomerThread;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 15075
 * @date 2021/4/6 08:56:32
 * @discription
 */
@RestController
public class HappyRestaurantController {
    Shop shop = new Shop();

    @GetMapping("/happyRestaurant")
    public String addEmp(Employee employee, Model model){
        MakeCustomerThread makeCustomer = new MakeCustomerThread(shop);
        Thread makeCustomerThread =new Thread(makeCustomer);
        makeCustomerThread.start();

        ChairThread ChairThread = new ChairThread(shop);
        Thread chairThread =new Thread(ChairThread);
        chairThread.start();

        ChefThread makeChef = new ChefThread(shop);
        Thread makeChefThread =new Thread(makeChef);
        makeChefThread.setName("厨师1");
        makeChefThread.start();

        Thread makeChefThread2 =new Thread(makeChef);
        makeChefThread2.setName("厨师2");
        makeChefThread2.start();

        Thread makeChefThread3 =new Thread(makeChef);
        makeChefThread3.setName("厨师3");
        makeChefThread3.start();
        return "";
    }

}


