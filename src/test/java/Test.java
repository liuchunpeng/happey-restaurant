/**
 * @author 15075
 * @date 2021/4/8 09:31:54
 * @discription
 */
public class Test {
    public static void main(String[] args) {
        // 共享资源
        ShapedResource resource = new ShapedResource();

        new Thread(new Producer(resource)).start();
        new Thread(new Consumer(resource)).start();
    }
}

 class ShapedResource {
     private String name;
     private String gender;
     private boolean isEmpty = true;
     // 表示共享资源对象是否为空

     // 生产者生产资源
     // synchroize 可以理解为线程互斥实现同步,在该线程进入资源区,其他线程只能等

      public void push(String name, String gender) {
         try {
             while (!isEmpty) { // 当不空时 ,等消费者来获取数据
                 this.wait();  // 生产完了,释放锁
                 // 使用同步锁对象来调用,表示当前进程释放同步锁,进入等待池
             }
             // 生成开始
             this.name = name;
             Thread.sleep(10);
             this.gender = gender;
             this.notify(); // 唤醒一个消费者
             isEmpty = false; // 现在共享资源中数据不为空了
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }

     // 消费者消费资源
      public void pop() {
         try {
             while (isEmpty) {
                 this.wait();
             }
             // 消费开始
             System.out.println(this.name + "-" + this.gender);
             Thread.sleep(10);
             // 消费结束
             this.notify(); // 唤醒一个生产者
             isEmpty = true;
         } catch (InterruptedException e) {

             e.printStackTrace();
         }

     }
 }

 class Consumer implements Runnable {
    private ShapedResource resource = null;

    public Consumer(ShapedResource resource) {
        this.resource = resource;
    }

    public void run() {
        for (int i = 0; i < 50; i++) {
            resource.pop();
        }
    }
}


 class Producer implements Runnable {
    private ShapedResource resource = null;

    public Producer(ShapedResource resource) {
        this.resource = resource;
    }

    public void run() {

        for (int i = 0; i < 50; i++) {
            if (i % 2 == 0) {

                resource.push("张汉森", "男");
            } else {
                resource.push("马浩峰", "女");
            }
        }
    }
}