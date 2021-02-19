package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

import java.util.Arrays;

public class SetAssociativeMapping extends MappingStrategy{

    Transformer t = new Transformer();
    private int SETS=512; // 共256个组
    private int setSize=2;   // 每个组4行


    /**
     * 该方法会被用于测试，请勿修改
     * @param SETS
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     * @param setSize
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    /**
     *
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */

    @Override
    public char[] getTag(int blockNO) {
        String tagStr = t.intToBinary(String.valueOf(blockNO / this.SETS)).substring(32 - (22 - shift()));
        for(int i = tagStr.length(); i < 22; ++i){
            tagStr = tagStr + "0";
        }
        return tagStr.toCharArray();
    }

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        int no = blockNO % this.SETS;
        return this.replacementStrategy.isHit(no * this.setSize, (no + 1) * this.setSize, getTag(blockNO));
    }

    @Override
    public int writeCache(int blockNO) {
        int no = blockNO % this.SETS;
        return this.replacementStrategy.Replace(no * this.setSize, (no + 1) * this.setSize, getTag(blockNO),
                Memory.getMemory().read(t.intToBinary(String.valueOf(blockNO)).substring(10) + "0000000000", 1024));
    }

    @Override
    public String getPAddr(int rowNo) {
        String result;
        result = String.valueOf(Cache.getCache().getcache().get(rowNo).getTag()).substring(0, 22 - shift());
        if(shift() != 0){
            result = result + t.intToBinary(String.valueOf(rowNo / this.setSize)).substring(32 - shift());
        }
        result = result + "0000000000";
        return result;
    }

    public int shift() {
        int temp = this.SETS;
        int n = 0;
        while(temp != 1){
            n++;
            temp /= 2;
        }
        return n;
    }

}










