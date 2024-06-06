package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorTest {

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
     * 创建节点 （持久、顺序、临时、数据）
     */
    @Test
    public void testCreate() throws Exception {
        // 1.基本创建 (如果没有指定数据，则默认将当前客户端ip作为数据存储)
        // String s = client.create().forPath("/app1");

        // String s = client.create().forPath("/app2", "hello".getBytes());

        // 创建临时节点
        client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3");

        // 创建多级节点
        // creatingParentContainersIfNeeded: 如果父节点不存在则创建父节点
        client.create().creatingParentContainersIfNeeded().forPath("/app4/p1");
    }


    /**
     * get  client.getData().forPath
     * ls   client.getChildren().forPath
     * ls -l  client.getData().storingStatIn(status)
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
//        byte[] bytes = client.getData().forPath("/app1");
//        System.out.println(new String(bytes));

        // 查询子节点
//        List<String> path = client.getChildren().forPath("/app4");
//        System.out.println(path);

        // 查询节点状态信息
        Stat status = new Stat();
        client.getData().storingStatIn(status).forPath("/app1");
        System.out.println(status);
    }



    @Test
    public void testSet() throws Exception {
        //client.setData().forPath("/app1", "it".getBytes());

        // 根据版本修改
        Stat status = new Stat();
        client.getData().storingStatIn(status).forPath("/app1");
        // 查询出version
        int version = status.getVersion();
        System.out.println(version);

        client.setData().withVersion(version).forPath("/app1", "it123".getBytes());
    }



    @Test
    public void testDelete() throws Exception {
        // client.delete().forPath("/app1");

        // 递归删除
        // client.delete().deletingChildrenIfNeeded().forPath("/app4");

        // 必须删除成功
        // client.delete().guaranteed().forPath("/app2");

        // 删除回调
        client.delete().guaranteed().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("被删除了");
                System.out.println(event);
            }
        }).forPath("/app1");
    }



    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
