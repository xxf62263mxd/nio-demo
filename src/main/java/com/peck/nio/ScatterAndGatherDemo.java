package com.peck.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ScatterAndGatherDemo {
    public static void main(String[] args) throws Exception{

        RandomAccessFile randomAccessFile1 = new RandomAccessFile("D:/os1.iso","rw");
        RandomAccessFile randomAccessFile2 = new RandomAccessFile("D:/os2.iso","rw");
        FileChannel fileChannel1 = randomAccessFile1.getChannel();
        FileChannel fileChannel2 = randomAccessFile2.getChannel();

        ByteBuffer[] bfs = new ByteBuffer[3];
        bfs[0] = ByteBuffer.allocate(256);
        bfs[1] = ByteBuffer.allocate(512);
        bfs[2] = ByteBuffer.allocate(1024);

        fileChannel1.read(bfs);  //分散读取

        for(ByteBuffer bf :bfs){
            bf.flip();  //切换成读取模式
        }
        fileChannel2.write(bfs); //聚集写入
        fileChannel1.close();
        fileChannel2.close();
        randomAccessFile1.close();
        randomAccessFile2.close();
    }
}
