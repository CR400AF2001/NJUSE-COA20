package memory.cacheReplacementStrategy;

import memory.Cache;
import memory.cacheWriteStrategy.WriteBackStrategy;
import memory.cacheWriteStrategy.WriteStrategy;

import java.util.Arrays;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] tag) {
        for(int i = start; i < end; ++i){
            if(Cache.getCache().getcache().get(i) != null && Cache.getCache().getcache().get(i).getValidBit() &&
                    Arrays.equals(Cache.getCache().getcache().get(i).getTag(), tag)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int Replace(int start, int end, char[] tag, char[] input) {
        long minTime = Long.MAX_VALUE;
        int min = -1;
        for(int i = start; i < end; ++i){
            long curTime = Cache.getCache().getcache().get(i).getTimeStamp();
            if(curTime < minTime){
                minTime = curTime;
                min = i;
            }
        }
        if(this.writeStrategy.isWriteBack(min)){
            this.writeStrategy.writeBack(min);
        }
        Cache.getCache().getcache().get(min).load(tag, input);
        return min;
    }


}
