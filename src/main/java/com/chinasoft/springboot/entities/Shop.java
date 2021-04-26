package com.chinasoft.springboot.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 15075
 * @date 2021/4/7 08:50:40
 * @discription
 */
@Data
public class Shop {

    Menu menu = getMenu();

    List<Food> foods = new ArrayList<>();

    List<Chef> chefs = new ArrayList<>();

    /**排队中的顾客*/
    List<Customer> customers = new LinkedList<>();

    /**全部的顾客*/
    List<Customer> allCustomers = new LinkedList<>();

    List<Table> tables = getTables();

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    private static List<Table> getTables() {
        Table table1 = new Table();
        table1.setTid(1);
        Chair chai1 = new Chair();
        chai1.setCid(1);
        Chair chai2 = new Chair();
        chai2.setCid(2);

        List<Chair> table1Char = new ArrayList<>() ;
        table1Char.add(chai1);
        table1Char.add(chai2);
        table1.setChairs(table1Char);

        Table table2 = new Table();
        table2.setTid(2);
        Chair chai3 = new Chair();
        chai3.setCid(3);
        Chair chai4 = new Chair();
        chai4.setCid(4);

        List<Chair> table2Char = new ArrayList<>() ;
        table2Char.add(chai3);
        table2Char.add(chai4);
        table2.setChairs(table2Char);

        List<Table> tables = new ArrayList<>();
        tables.add(table1);
        tables.add(table2);

        return  tables;
    }

    private static Menu getMenu() {
        Menu menu = new Menu();
        List<Food> cai = new ArrayList<>();
        Food food1 = new Food();
        food1.setFoodId(1);
        food1.setFoodName("意大利面1");

        Food food2 = new Food();
        food2.setFoodId(2);
        food2.setFoodName("小鸡炖蘑菇2");

        Food food3 = new Food();
        food3.setFoodId(3);
        food3.setFoodName("黄焖鸡3");

        Food food4 = new Food();
        food4.setFoodId(4);
        food4.setFoodName("咖喱土豆4");

        cai.add(food1);
        cai.add(food2);
        cai.add(food3);
        cai.add(food4);
        menu.setMenu(cai);

        return menu;
    }

    public void addCustomer(){
        try {
            //平均每5秒招揽一位顾客
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Customer customer = new Customer();
        customer.setCusId(allCustomers.size()+1);
        customer.setCusName("顾客"+(allCustomers.size()+1));

        lock.lock();
        try {
            customers.add(customer);
            allCustomers.add(customer);
            condition.signal();
        } finally {
            lock.unlock();
        }

        System.out.println(customer.getCusName() + "光顾了本店");

    }

    public void paiChair() throws InterruptedException {
        lock.lock();
        try {
            boolean isEmpty =false;
            out:
            for (Table table:tables) {
                for (Chair chair : table.getChairs()) {

                    //椅子上无顾客就放进等待中的顾客
                    if (chair.getCustomer() == null) {
                        isEmpty =true;
                        break out;
                    }
                }
            }

            if (customers.size()<=0 || !isEmpty ){
                condition.await();
            }else {

                outer:
                for (Table table:tables)  {
                    for (Chair chair: table.getChairs()) {
                        System.out.println(chair);
                        //椅子上无顾客就放进等待中的顾客
                        if (chair.getCustomer() == null){

                            Customer customer = customers.get(0);

                            //等待的第一个入座
                            chair.setCustomer(customer);

                            System.out.println(customer.getCusName()+"坐在了"
                                    +table.getTid()+ "号桌的" + chair.getCid()+"号椅子上");

                            //客人从等待队列中移除
                            customers.remove(0);
                            Random r = new Random(1);
                            Integer ran1 = r.nextInt(menu.getMenu().size());
                            Food food = new Food();
                            Food menuFood = menu.getMenu().get(ran1);
                            food.setFoodNo(foods.size()+1);
                            food.setFoodName(menuFood.getFoodName());
                            food.setFoodId(menuFood.getFoodId());
                            food.setByCustomer(customer);

                            foods.add(food);
                            System.out.println("顾客"+customer.getCusName()+"点了" + food.getFoodName());

                            //唤醒厨师线程
                            condition.signal();

                            System.out.println("剩余等待客人：" + customers.size() + "人");

                            break outer;
                        }

                    }
                }

            }
        }finally {
            lock.unlock();
        }

    }

    public void zuoCai(){

        try {
            if (foods.size()==0){
                lock.lock();
                try {
                    condition.await();
                } finally {
                    lock.unlock();
                }

            }else {

                Food food = foods.get(0);
                lock.lock();
                try {
                    foods.remove(0);
                    System.out.println(Thread.currentThread().getName()+"取走了"+food.getByCustomer().getCusName()+"所点的菜品单："+food.getFoodName());
                    System.out.println("剩余点餐单："+foods);
                } finally {
                    lock.unlock();
                }

                Thread.sleep(10000);
                System.out.println(Thread.currentThread().getName()+"用了10秒做好了"+food.getByCustomer().getCusName()+"的食物"+food.getFoodName());
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName()+"用了3秒将食物"+food.getFoodName() + "送到了顾客手中");

                Thread cusThread = new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        outer:
                        for (Table table:tables) {
                            for (Chair chair : table.getChairs()) {
                                if (chair.getCustomer().getCusId().equals(food.getByCustomer().getCusId())){
                                    chair.setCustomer(null);
                                    System.out.println(food.getByCustomer().getCusName()+"吃完了" + food.getFoodName());
                                    break outer;
                                }

                            }
                        }
                        lock.lock();
                        try {
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });
                cusThread.start();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
