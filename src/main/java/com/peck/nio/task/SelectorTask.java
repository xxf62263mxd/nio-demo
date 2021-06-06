package com.peck.nio.task;

import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class SelectorTask implements Callable {

    private Selector selector;
    private ExecutorService executorService;
    public SelectorTask(Selector selector, ExecutorService executorService){
        System.out.println("Selector线程启动...");
        this.selector = selector;
        this.executorService = executorService;
    }

    @Override
    public Object call() throws Exception {

        while(selector.select() > 0){   //轮询检测是否有注册的事件发生
            //获取所有触发的事件
            Set<SelectionKey> sks = selector.selectedKeys();
            //处理所有触发的事件
            Iterator<SelectionKey> it = sks.iterator();
            while(it.hasNext()) {
                SelectionKey sk = it.next();
                //Selector.select()取出事件集和的全部事件
                //如果不删除，在下次轮询的时候，调用Selector.select()会取出旧的事件集，导致重复处理
                //因此应该remove掉当前SelectionKey
                it.remove();
                //必须在该线程中处理掉事件，Java NIO属于水平触发（条件触发），若事件没有被处理，则该事件会被无限触发
                if (sk.isAcceptable()) {  //触发了接收事件
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) sk.channel();  //获取被触发事件的通道
                    SocketChannel socketChannel = serverSocketChannel.accept();//处理掉Accept事件，不然将一直触发Accept事件
                    //设置为非阻塞模式
                    socketChannel.configureBlocking(false);
                    //注册read事件,不能在其他线程中注册,否则register与selet方法会造成死锁
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    //处理业务逻辑
                    executorService.submit(new AcceptTask(selector, socketChannel));
                } else if (sk.isReadable()) {  //触发了读取就绪事件
                    SocketChannel socketChannel = (SocketChannel) sk.channel();  //获取被触发事件的通道

                    StringBuffer sb = new StringBuffer();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);//创建缓冲区
                    //读取干净内核缓存区中的数据，否则将一直触发read事件
                    int len = -1;   //len>0 代表有数据读出 len=0 代表无数据可读但通道连接为断，等待下次的read事件触发
                    while((len = socketChannel.read(byteBuffer)) > 0 ){
                        byteBuffer.flip();
                        //接受数据
                        sb.append(StandardCharsets.UTF_8.decode(byteBuffer));
                        byteBuffer.clear();
                    }

                    //有数据则触发业务逻辑
                    if(sb.length() > 0){
                        executorService.submit(new ReadTask(selector, socketChannel,sb.toString()));
                    }

                    //len=-1 当Client端的通道close时，read事件将会就绪,并且读取长度为-1，用于通知服务器关闭通道或至少注销事件
                    if(len == -1){
                        System.out.println("连接关闭");
                        socketChannel.close();
                    }

                }//可以继续添加其他需要处理的已注册事件..




            }
        }

        System.out.println("Selector线程结束...");
        return null;
    }
}
