package memory.cacheReplacementStrategy;

import memory.Cache;

/**
 * 最近不经常使用算法
 */
public class LFUReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] addrTag) {
        for(int i=start;i<=end;i++){
            if(Cache.getCache().isMatch( i,addrTag )){   // 命中该行
                // 增加该行的访问次数 visited
                Cache.getCache().addVisited( i );
                return i; // 返回该行
            }
        }

        // 没有命中
        return -1;
    }

    @Override
    public int Replace(int start, int end, char[] addrTag, char[] input) {
        int minVisited = Integer.MAX_VALUE;     // visited最小值
        int minIndex = -1;       // visited最小值对应的下标

        for(int i=start;i<=end;i++){
            int curVisited = Cache.getCache().getVisited( i );
            if( curVisited < minVisited ){
                minVisited = curVisited;
                minIndex = i;
            }
        }

        Cache.getCache().update( minIndex, addrTag, input );
        return minIndex;
    }
}
