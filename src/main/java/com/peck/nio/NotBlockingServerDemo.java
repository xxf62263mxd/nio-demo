package com.peck.nio;

import com.peck.nio.task.SelectorTask;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NotBlockingServerDemo {

    private static ServerSocketChannel serverSocketChannel;
    private static Selector selector;
    private static ExecutorService executorService = null;
    public static void main(String[] args) throws Exception {
        //初始化
        executorService = Executors.newFixedThreadPool(3);
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9001));

        //Channel开启非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //注册事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//注册接受Client事件

        //由一条线程来运行Selector
        SelectorTask selectorTask = new SelectorTask(selector,executorService);
        executorService.submit(selectorTask);

    }


}
