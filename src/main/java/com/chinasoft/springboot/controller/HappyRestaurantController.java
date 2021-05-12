package com.chinasoft.springboot.controller;

import com.chinasoft.springboot.entities.*;
import com.chinasoft.springboot.thread.ChairThread;
import com.chinasoft.springboot.thread.ChefThread;
import com.chinasoft.springboot.thread.MakeCustomerThread;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 15075
 * @date 2021/4/6 08:56:32
 * @discription
 */
@RestController
public class HappyRestaurantController {

    Map<String,Shop> userShop = new HashMap<>();

    @GetMapping("/happyRestaurant")
    public String happyRestaurant(String user){
        Shop shop = new Shop();
        userShop.put(user,shop);

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

    @GetMapping("/addMenu")
    public void addMenu(String user) throws InterruptedException {
        Menu menu = new Menu();
        List<Food> cai = new ArrayList<>();

        Food food5 = new Food();
        food5.setFoodId(5);
        food5.setFoodName("红烧鸡块5");

        cai.add(food5);
        menu.setMenu(cai);

        userShop.get(user).setMenu(menu);
    }

}


