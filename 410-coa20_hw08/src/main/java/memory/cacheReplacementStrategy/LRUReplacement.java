package memory.cacheReplacementStrategy;

import memory.Cache;

/**
 * 最近最少用算法
 */
public class LRUReplacement extends ReplacementStrategy {

    /**
     *
     * @param start 起始位置
     * @param end 结束位置 闭区间
     */
    @Override
    public int isHit(int start, int end,char[] addrTag) {

        for(int i=start;i<=end;i++){
            if(Cache.getCache().isMatch( i,addrTag )){   // 命中该行
                // 重置该行时间戳
                Cache.getCache().setTimeStamp( i );
                return i; // 返回此行
            }
        }

        // 没有命中
        return -1;
    }


    /**
     * 找到最小时间戳的行，替换
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    @Override
    public int Replace(int start, int end, char[] addrTag, char[] input) {
        long minTime = Long.MAX_VALUE;
        int minIndex = -1;
        for(int i=start; i<=end; i++){
            long curTime = Cache.getCache().getTimeStamp( i );
            if( curTime < minTime ){
                minTime = curTime;
                minIndex = i;
            }
        }
        Cache.getCache().update( minIndex, addrTag, input );
        return minIndex;
    }

}





























