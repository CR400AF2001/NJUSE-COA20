package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

public class AssociativeMapping extends MappingStrategy {  // 全相联映射

    static final int CACHE_LINE = Cache.CACHE_SIZE_B/ Cache.LINE_SIZE_B; // 共1024个行
    Transformer t = new Transformer();

    /**
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前22位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        int tag = blockNO;
        String tagStr = t.intToBinary( ""+tag ).substring( 10,32 );
        return tagStr.toCharArray();
    }

    @Override
    public int map(int blockNO) {
        char[] addrTag = getTag( blockNO );   // 获得内存地址blockNO所对应的tag
        return this.replacementStrategy.isHit( 0, CACHE_LINE-1, addrTag );
    }

    @Override
    public int writeCache(int blockNO) {
        char[] addrTag = getTag( blockNO );
        return this.replacementStrategy.Replace( 0, CACHE_LINE-1, addrTag, Memory.getMemory().read(t.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B) );
    }

    @Override
    public String getPAddr(int blockNo) {
        return null;
    }
}
