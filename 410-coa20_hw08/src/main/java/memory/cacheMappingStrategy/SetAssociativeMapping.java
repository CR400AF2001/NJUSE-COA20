package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

public class SetAssociativeMapping extends MappingStrategy {


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
        int tag = blockNO / SETS;
        String tagStr = t.intToBinary( ""+tag ).substring( 10 + getPow() ,32 );
        int diff = 22 - tagStr.length();
        for(int i=0;i<diff; i++){
            tagStr = tagStr +"0";
        }
        return tagStr.toCharArray();
    }
    private int getPow(){
        int temp = SETS;
        int i = 0;
        while(temp > 0){
            i ++;
            temp /= 2;
        }
        return i-1;
    }

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        int setNO = blockNO % SETS;           // 获得内存地址blockNO所对应的组号setNO
        char[] addrTag = getTag( blockNO );   // 获得内存地址blockNO所对应的tag
        if (SETS == 1024) {
            if( Cache.getCache().isMatch( setNO, addrTag ) ){  // 命中返回row
                return setNO;
            }
            return -1;
        }
        return this.replacementStrategy.isHit( setNO*setSize, (setNO+1)*setSize-1, addrTag );
    }

    @Override
    public int writeCache(int blockNO) {
        int setNO = blockNO % SETS;
        char[] addrTag = getTag( blockNO );
        if (SETS == 1024) {
            Cache.getCache().update( setNO,addrTag, Memory.getMemory().read(t.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B));
            return setNO;
        }
        return this.replacementStrategy.Replace(setNO*setSize, (setNO+1)*setSize-1, addrTag , Memory.getMemory().read(t.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B));
    }

    @Override
    public String getPAddr(int rowNo) {
        String setNo = t.intToBinary("" + rowNo / setSize).substring(32 - getPow(), 32);
        char[] tag = Cache.getCache().getTag(rowNo);
        String addr = new String(tag).substring(0, tag.length-getPow()) + setNo + "0000000000";
        return addr;
    }

}










