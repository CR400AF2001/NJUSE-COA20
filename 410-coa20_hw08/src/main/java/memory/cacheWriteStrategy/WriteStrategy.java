package memory.cacheWriteStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.MappingStrategy;

/**
 * @CreateTime: 2020-11-12 11:38
 */
public abstract class WriteStrategy {
    MappingStrategy mappingStrategy;
    /**
     * 将数据写入Cache，并且根据策略选择是否修改内存
     * @param rowNo 行号
     * @param input  数据
     * @return
     */
    public String write(int rowNo, char[] input) {
        Cache.getCache().UpdateCacheLineData(rowNo, input);
        if(isWriteBack()){
            writeBack(rowNo);
        }
        return null;
    }


    /**
     * 修改内存
     * @return
     */
    public void writeBack(int rowNo) {
        String addr = mappingStrategy.getPAddr(rowNo);
        Memory.getMemory().write(addr, Cache.LINE_SIZE_B, Cache.getCache().getDate(rowNo));
    }

    public void setMappingStrategy(MappingStrategy mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
    }

    public abstract Boolean isWriteBack();
}
