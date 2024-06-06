package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class Ticket implements Runnable {

    private int tickets = 10; // 数据库票数

    private InterProcessMutex lock;

    public Ticket() {
        String connectString = "121.43.63.185:2181";
        int sessionTimeoutMs = 60 * 1000;
        int connectionTimeoutMs = 15 * 1000;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(connectString).connectionTimeoutMs(connectionTimeoutMs).
                sessionTimeoutMs(sessionTimeoutMs).retryPolicy(retryPolicy).build();
        client.start();

        lock = new InterProcessMutex(client, "/lock");
    }

    @Override
    public void run() {
        while (true) {
            // 获取锁
            try {
                lock.acquire(3, TimeUnit.SECONDS);
                if (tickets > 0) {
                    System.out.println(Thread.currentThread().getName()+":"+tickets);
                    Thread.sleep(100);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
