package memory.cacheReplacementStrategy;

import memory.Cache;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] addrTag) {
        for(int i=start;i<=end;i++){
            if(Cache.getCache().isMatch( i,addrTag )){   // 命中该行
                return i; // 返回该行
            }
        }

        // 没有命中
        return -1;
    }

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
        if(Cache.getCache().isDirty(minIndex) && Cache.getCache().isValid(minIndex)){
            writeStrategy.writeBack(minIndex);
        }
        Cache.getCache().update( minIndex, addrTag, input );
        return minIndex;
    }


}
