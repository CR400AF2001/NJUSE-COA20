package memory;


import transformer.Transformer;

import java.io.*;
import java.util.Arrays;

/**
 * 磁盘抽象类，磁盘大小为128MB
 */
public class Disk implements DiskInterface{
    public static int DISK_SIZE_B = 128 * 1024 * 1024;      // 磁盘大小 128 MB

    private static Disk diskInstance = new Disk();

//	private static char[] disk = new char[DISK_SIZE_B];

    private static File disk_device;

    private Disk() {
        disk_device = new File("DISK.vdev");
        if (disk_device.exists()) {
            disk_device.delete();
            try {
                disk_device.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        try {
            disk_device.createNewFile();
            // 初始化磁盘
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(disk_device)));
            char[] dataUnit = new char[1024];
            Arrays.fill(dataUnit, (char) 0b00001111);
            for (int i=0; i<20; i++) {
                for (int j=0; j<1024; j++) {
                    writer.write(dataUnit);
                }
            }
            Arrays.fill(dataUnit, (char) 0b00000011);
            for (int i=0; i<12; i++) {
                for (int j=0; j<1024; j++) {
                    writer.write(dataUnit);
                }
            }
            Arrays.fill(dataUnit, (char) 0b01010101);
            for (int i=0; i<32; i++) {
                for (int j=0; j<1024; j++) {
                    writer.write(dataUnit);
                }
            }
            Arrays.fill(dataUnit, (char) 0b00110011);
            for (int i=0; i<16; i++) {
                for (int j=0; j<1024; j++) {
                    writer.write(dataUnit);
                }
            }
            Arrays.fill(dataUnit, (char) 0b00000000);
            for (int i=0; i<48; i++) {
                for (int j=0; j<1024; j++) {
                    writer.write(dataUnit);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Disk getDisk() {
        return diskInstance;
    }

    public char[] read(String eip, int len){
        char[] data = new char[len];
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile(disk_device, "r");
            // 本作业只有128M磁盘，不会超出int表示范围
            // ps: java的char是两个字节，但是write()方法写的是字节，因此会丢掉char的高8-bits，读的时候需要用readByte()
            // pss: 读磁盘会很慢，请尽可能减少read函数调用
            // psss: 这几行注释你们其实不需要看
            reader.skipBytes(Integer.parseInt(new Transformer().binaryToInt("0" + eip)));
            for (int i=0; i<len; i++) {
                data[i] = (char) reader.readByte();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public void write(String eip, int len, char[] data){
        RandomAccessFile writer = null;
        try {
            writer = new RandomAccessFile(disk_device, "rw");
            writer.skipBytes(Integer.parseInt(new Transformer().binaryToInt("0" + eip)));
            for (int i=0; i<len; i++) {
                writer.write(data[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 将Byte流转换成Bit流
     * @param data
     * @return
     */
    public static char[] ToBitStream(char[] data) {
        char[] t = new char[data.length * 8];
        int index = 0;
        for (char datum : data) {
            for (int j = 0; j < 8; j++) {
                t[index++] = (char) (((datum >> (7 - j)) & (0b00000001)) + '0');
            }
        }
        return t;
    }

    /**
     * 将Bit流转换为Byte流
     * @param data
     * @return
     */
    public static char[] ToByteStream(char[] data) {
        char[] t = new char[data.length / 8];
        int j = 0;
        int index = 0;
        for (char datum : data) {
            t[index] = (char) (( datum-'0' << (7-j) ) | t[index]);
            j++;
            if (j % 8 == 0) {
                index ++;
                j = 0;
            }
        }
        return t;
    }
}
