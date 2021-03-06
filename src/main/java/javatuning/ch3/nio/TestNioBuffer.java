package javatuning.ch3.nio;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

public class TestNioBuffer extends TestMapBuffer {

    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (res & 0xff);
        targets[2] = (byte) ((res >> 8) & 0xff);
        targets[1] = (byte) ((res >> 16) & 0xff);
        targets[0] = (byte) (res >>> 24);
        return targets;
    }

    public static int byte2int(byte b1, byte b2, byte b3, byte b4) {
        return ((b1 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 8) | (b4 & 0xff);
    }

    @Test
    public void testBufferWrite() throws IOException {
        long begTime = System.currentTimeMillis();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("./data/temp.tmp")), 16 * 1024 * 1024));
        FileOutputStream fos = new FileOutputStream(new File("./data/temp_buffer.tmp"));
        FileChannel fc = fos.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
        for (int i = 0; i < numOfInts; i++) {
            byteBuffer.put(int2byte(i));
        }
        byteBuffer.flip();
        fc.write(byteBuffer);
        long endTime = System.currentTimeMillis();
        System.out.println("testBufferWrite:" + (endTime - begTime) + "ms");
    }

    @Test
    public void testBufferRead() throws IOException {
        long begTime = System.currentTimeMillis();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("./data/temp.tmp")), 16 * 1024 * 1024));
        FileInputStream fis = new FileInputStream(new File("./data/temp_buffer.tmp"));
        FileChannel fc = fis.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
        fc.read(byteBuffer);
        fc.close();
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            byte2int(byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("testBufferRead:" + (endTime - begTime) + "ms");
    }

    @Test
    public void testBufferWriteInt() throws IOException {
        long begTime = System.currentTimeMillis();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("./data/temp.tmp")), 16 * 1024 * 1024));
        FileOutputStream fos = new FileOutputStream(new File("./data/temp_buffer_int.tmp"));
        FileChannel fc = fos.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
        for (int i = 0; i < numOfInts; i++) {
            byteBuffer.put(int2byte(i));
        }
        byteBuffer.flip();
        fc.write(byteBuffer);
        long endTime = System.currentTimeMillis();
        System.out.println("testBufferWriteInt: " + (endTime - begTime) + "ms");
    }

    @Test
    public void testBufferReadInt() throws IOException {
        long begTime = System.currentTimeMillis();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("./data/temp.tmp")), 16 * 1024 * 1024));
        FileInputStream fis = new FileInputStream(new File("./data/temp_buffer_int.tmp"));
        FileChannel fc = fis.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
        fc.read(byteBuffer);
        fc.close();
        byteBuffer.flip();
        IntBuffer ib = byteBuffer.asIntBuffer();
        while (ib.hasRemaining()) {
            ib.get();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("testBufferReadInt: " + (endTime - begTime) + "ms");
    }

}
