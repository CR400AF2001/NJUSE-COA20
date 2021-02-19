package memory;

import transformer.Transformer;

/**
 * 内存抽象类
 * TODO: 程序加载,分页/分段/段页式
 * TODO: 内存数据结构抽象
 */
public class Memory implements MemoryInterface {
    private static int MEM_SIZE_B = 32 * 1024 * 1024;      // 32 MB

    private static char[] memory = new char[MEM_SIZE_B];

    private static Memory memoryInstance = new Memory();

    private Memory() {}

    public static Memory getMemory() {
        return memoryInstance;
    }

    public char[] read(String eip, int len){
        char[] data = new char[len];
        for (int ptr=0; ptr<len; ptr++) {
            data[ptr] = memory[Integer.parseInt(new Transformer().binaryToInt(eip)) + ptr];
        }
        return data;
    }

    public void write(String eip, int len, char []data){
        // 通知Cache缓存失效
        Cache.getCache().invalid(eip, len);
        // 更新数据
        for (int ptr=0; ptr<len; ptr++) {
            memory[Integer.parseInt(new Transformer().binaryToInt(eip)) + ptr] = data[ptr];
        }
    }
}




















































