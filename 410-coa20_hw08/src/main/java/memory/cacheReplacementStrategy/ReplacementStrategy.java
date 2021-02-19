package memory.cacheReplacementStrategy;

import memory.cacheWriteStrategy.WriteStrategy;

public abstract class ReplacementStrategy {

    WriteStrategy writeStrategy;
    /**
     * 在start-end范围内查找是否命中
     * @param start 起始行
     * @param end 结束行 闭区间
     */
    public abstract int isHit(int start, int end, char[] addrTag);

    /**
     * 在未命中的情况下将内存中的数写入cache
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    public abstract int Replace(int start, int end, char[] addrTag, char[] input);

    public void setWriteStrategy(WriteStrategy writeStrategy) {
        this.writeStrategy = writeStrategy;
    }
}
