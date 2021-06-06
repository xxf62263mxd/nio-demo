package com.peck.nio.task;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

public class ReadTask implements Callable {

    private Selector selector;
    private SocketChannel socketChannel;
    private Object data;

    public ReadTask(Selector selector, SocketChannel socketChannel,Object data) {
        this.selector = selector;
        this.socketChannel = socketChannel;
        this.data = data;
    }

    @Override
    public Object call() throws Exception {
        System.out.println("执行连读取客户端数据后的业务逻辑："+(String)data);
        return null;
    }
}
