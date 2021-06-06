package com.peck.nio.task;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

public class AcceptTask implements Callable {

    private Selector selector;
    private SocketChannel socketChannel;

    public AcceptTask(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @Override
    public Object call() throws Exception {
        System.out.println("执行连接客户端后的业务逻辑");
        return null;
    }
}
