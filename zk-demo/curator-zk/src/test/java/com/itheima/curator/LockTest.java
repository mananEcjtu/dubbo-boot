package com.itheima.curator;

public class LockTest {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        // 创建客户端
        Thread t1 = new Thread(ticket,"携程");
        Thread t2 = new Thread(ticket, "飞猪");

        t1.start();
        t2.start();
    }
}
