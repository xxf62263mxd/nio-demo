package com.peck.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class NotBlockingClientDemo {
    public static void main(String[] args) throws Exception{
        //连接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9001));
        Scanner sc = new Scanner(System.in);

        //将通道设置为非阻塞模式
        socketChannel.configureBlocking(false);


        ByteBuffer byteBuffer = ByteBuffer.allocate(10);


        while(sc.hasNext()){
            byteBuffer.put(sc.nextLine().getBytes(StandardCharsets.UTF_8));
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //调用通道的close方法会触发服务端的read事件，并且read方法的返回值为-1，通知服务端进行连接的关闭
        //若服务端不处理该事件，则会无限读事件循环
        socketChannel.close();

    }
}
