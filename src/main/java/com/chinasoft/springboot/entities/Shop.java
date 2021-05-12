package com.chinasoft.springboot.entities;

import lombok.Data;
import org.springframework.util.ObjectUtils;

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

    /**
     * 菜单初始化
     */
    Menu menu = initMenu();

    /**
    等待制作的菜品
     */
    List<Food> foods = new ArrayList<>();

    /**
     * 今日订单数计数器
     */
    Integer foodSize = 0;

    /**排队中的顾客*/
    List<Customer> customers = new LinkedList<>();

    /**全部的顾客*/
    List<Customer> allCustomers = new LinkedList<>();

    /**桌椅*/
    List<Table> tables = getTables();

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    /**
     * 模拟餐厅座椅数据
     * @return
     */
    private List<Table> getTables() {
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

    /**
     * 菜单初始化
     * @return
     */
    private Menu initMenu() {
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

    /**
     * 模拟餐厅进入客人
     */
    public void addCustomer(){
        try {
            //平均每2秒招揽一位顾客
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
            //客人光临，唤醒椅子线程招待客人入座
            condition.signal();
        } finally {
            lock.unlock();
        }

        System.out.println(customer.getCusName() + "光顾了本店");

    }

    /**
     * 椅子线程监控椅子和顾客状态进行为客人分配座位
     * @throws InterruptedException
     */
    public void paiChair() throws InterruptedException {
        lock.lock();
        try {
            //有无空座标识
            boolean isEmpty =false;
            out:
            for (Table table:tables) {
                for (Chair chair : table.getChairs()) {

                    //判断当前有无空座
                    if (chair.getCustomer() == null) {
                        isEmpty =true;
                        break out;
                    }
                }
            }

            if (customers.size()<=0 || !isEmpty ){
                //没有顾客或者没有空座，进入等待
                condition.await();
            }
            //椅子上无顾客就请进等待中的顾客
            else {
                outer:
                for (Table table:tables)  {
                    for (Chair chair: table.getChairs()) {

                        //椅子上无顾客就放进等待中的顾客
                        if (chair.getCustomer() == null){

                            Customer customer = customers.get(0);

                            //等待的第一个入座
                            chair.setCustomer(customer);

                            System.out.println(customer.getCusName()+"坐在了"
                                    +table.getTid()+ "号桌的" + chair.getCid()+"号椅子上");

                            //客人从等待队列中移除
                            customers.remove(0);

                            Random r = new Random();
                            Integer ran1 = r.nextInt(menu.getMenu().size());

                            //模拟顾客点菜，随机生成一份点菜单
                            Food food = new Food();
                            Food menuFood = menu.getMenu().get(ran1);
                            food.setFoodNo(++foodSize);
                            food.setFoodName(menuFood.getFoodName());
                            food.setFoodId(menuFood.getFoodId());
                            food.setByCustomer(customer);

                            foods.add(food);
                            System.out.println("顾客"+customer.getCusName()+"点了" + food.getFoodName());

                            //客人点菜，唤醒厨师线程做菜
                            condition.signal();

                            break outer;
                        }
                    }
                }
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 模拟厨师做菜
     */
    public void zuoCai(){
        lock.lock();
        try {
            //没有等待制作的菜品时进入等待
            if (foods.size()==0){
                try {
                    condition.await();
                } finally {
                    lock.unlock();
                }

            }else {

                Food food = foods.get(0);
                try {
                    foods.remove(0);
                    System.out.println(Thread.currentThread().getName()+"取走了"+food.getByCustomer().getCusName()+"所点的菜品单："+food.getFoodName());
                    System.out.println("剩余点餐单："+foods);
                } finally {
                    lock.unlock();
                }

                //模拟做菜时间
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName()+"用了3秒做好了"+food.getByCustomer().getCusName()+"的食物"+food.getFoodName());

                //模拟传菜时间（这里传菜也是由厨师负责）
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName()+"用了3秒将食物"+food.getFoodName() + "送到了顾客手中");

                //为点了该菜品的顾客开始吃饭线程
                new Thread(() -> {
                    try {
                        //模拟吃饭时间
                        Thread.sleep(3000);
                        outer:
                        for (Table table:tables) {
                            for (Chair chair : table.getChairs()) {
                                if (!ObjectUtils.isEmpty(chair.getCustomer()) && food.getByCustomer().getCusId().equals(chair.getCustomer().getCusId())){
                                    chair.setCustomer(null);
                                    System.out.println(food.getByCustomer().getCusName()+"吃完了" + food.getFoodName());
                                    break outer;
                                }

                            }
                        }
                        lock.lock();
                        try {
                            //客人吃完饭离开，唤醒椅子线程接待客人入座
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                //模拟传菜回到厨房时间
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName()+"回到了厨房继续工作");

            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
