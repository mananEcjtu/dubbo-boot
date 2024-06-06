package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorWatcherTest {

    private CuratorFramework client;

    /**
     * 建立连接
     */
    @Before
    public void testConnect() {
        /*
         * connectString – 连接字符串 zk server和端口 121.43.63.185:2181,121.43.63.186:2181
         * sessionTimeoutMs – 会话超时时间，ms
         * connectionTimeoutMs – 连接超时时间，ms
         * retryPolicy – 重试策略
         */
        String connectString = "121.43.63.185:2181";
        int sessionTimeoutMs = 60 * 1000;
        int connectionTimeoutMs = 15 * 1000;

        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        // 1.第一种方式
        // CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);

        // 2.第二种方式
        client = CuratorFrameworkFactory.builder().
                connectString(connectString).connectionTimeoutMs(connectionTimeoutMs).
                sessionTimeoutMs(sessionTimeoutMs).namespace("itheima").retryPolicy(retryPolicy).build();
        // namespace指定根目录

        // 开启连接
        client.start();
    }



    /**
     * 给指定一个节点注册监听器
     */
    @Test
    public void testNodeCache() throws Exception {
        // 1.创建NodeCache对象
        final NodeCache nodeCache = new NodeCache(client, "/app1");
        // 2.注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化了");
                // 获取修改节点后的数据
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });
        // 3.开启监听
        // 设置位true，则开启监听时加载缓存数据
        nodeCache.start(true);

        while (true) {

        }
    }


    /**
     * 监听所有子节点
     * @throws Exception
     */
    @Test
    public void testPathChildrenCache() throws Exception {
        // 1.创建NodeCache对象
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/app1", true);

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("子节点变化了：");
                System.out.println(pathChildrenCacheEvent);
            }
        });
        pathChildrenCache.start();

        while (true) {

        }
    }


    /**
     * 监听某个节点自己和子节点
     */
    @Test
    public void test() throws Exception {
        final TreeCache treeCache = new TreeCache(client, "/app1");
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("节点变化");
                System.out.println(treeCacheEvent);
            }
        });
        treeCache.start();

        while (true) {

        }
    }





    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
