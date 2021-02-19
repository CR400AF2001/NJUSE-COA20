package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

/**
 * 直接映射 12位标记 + 10位块号 + 10位块内地址
 */
public class DirectMapping extends MappingStrategy {

    static final int CACHE_LINE = Cache.CACHE_SIZE_B/ Cache.LINE_SIZE_B; // 共1024个行

    Transformer t = new Transformer();

    /**
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前12位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        int tag = blockNO / CACHE_LINE;
        String tagStr = t.intToBinary( ""+tag ).substring( 20,32 );
        int diff = 22-tagStr.length();
        for(int i=0;i<diff;i++){
            tagStr = tagStr+"0";
        }
        return tagStr.toCharArray();
    }


    /**
     * 根据内存地址找到对应的行是否命中，直接映射不需要用到替换策略
     * @param blockNO
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        char[] tag = getTag( blockNO );
        int row = blockNO % CACHE_LINE;
        if( Cache.getCache().isMatch( row, tag ) ){  // 命中返回row
            return row;
        }
        return -1;      // 未命中
    }

    /**
     * 在未命中情况下重写cache，直接映射不需要用到替换策略
     * @param blockNO
     * @return
     */
    @Override
    public int writeCache(int blockNO) {
        char[] tag = getTag( blockNO );
        int row = blockNO % CACHE_LINE;
        Cache.getCache().update( row,tag, Memory.getMemory().read(t.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B));
        return row;
    }

    @Override
    public String getPAddr(int blockNo) {
        return null;
    }
}
