package cpu;

import memory.Disk;
import transformer.Transformer;

/**
 * @CreateTime: 2020-11-04 16:23
 */
public class TLB {


    public static final boolean isAvailable = true;			// 默认启用TLB

    public static final int TLB_SIZE_B = 4 * 1024;      // 4KB 总大小

    public static final int LINE_SIZE_B = 8; // 64 Bit = 8 Byte

    private TLBLinePool TLB = new TLBLinePool(TLB_SIZE_B/LINE_SIZE_B); 	// 总大小 4KB / 8B = 512行

    private TLB(){}

    public static TLB tlbInstance = new TLB();

    public static TLB getTLB() {return tlbInstance;}

    /**
     * if match return the index else return -1
     * @param pageNo 虚拟页号
     * @return TLB行号
     */
    public int isMatch(int pageNo){
        for(int i = 0;i<TLB_SIZE_B/LINE_SIZE_B;i++){
            Transformer transformer = new Transformer();
            String spageNo = transformer.intToBinary(String.valueOf(pageNo));
            if(spageNo.equals(new String(Disk.ToBitStream(TLB.get(i).getPage())))&&TLB.get(i).valid){
                return i;
            }

        }
        return -1;
    }

    /**
     * 获取物理页框
     * @param row TLB的行号，这个行号你应该使用上一个函数获取
     * @return
     */
    public char[] getFrameOfPage(int row){
        return Disk.ToBitStream(TLB.get(row).getPageFrame());
    }

    /**
     * 向TLB中写入数据
     * @param pageNo 虚拟页号
     * @param pageFrame 物理页号
     * @return
     */
    public int writeTLB(char[] pageNo, char[] pageFrame) {
        long minTime = Long.MAX_VALUE;
        int minIndex = -1;
        for(int i=0; i<TLB_SIZE_B/LINE_SIZE_B; i++){
            long curTime = getTimeStamp( i );
            if( curTime < minTime ){
                minTime = curTime;
                minIndex = i;
            }
        }
        if(minIndex != -1){
            TLB.get(minIndex).update(Disk.ToByteStream(pageNo), Disk.ToByteStream(pageFrame));
            return minIndex;
        }
        else return -1;
    }


    public void invalid(int row) {
        TLB.get(row).valid = false;
    }

    public int getTlbSize(){
        return TLB_SIZE_B/LINE_SIZE_B;
    }

    // 获取时间戳
    private long getTimeStamp(int row){
        TLBLine tlbLine = TLB.get( row );
        if (tlbLine.valid) {
            return tlbLine.timeStamp;
        }
        return -1;
    }



    private class TLBLinePool{
        TLBLinePool(int lines){
            tlbLines = new TLBLine[lines];
        }
        private TLBLine[] tlbLines;
        private TLBLine get(int lineNO){
            if(lineNO >= 0 && lineNO < tlbLines.length){
                TLBLine l = tlbLines[lineNO];
                if(l == null){
                    tlbLines[lineNO] = new TLBLine();
                    l = tlbLines[lineNO];
                }
                return l;
            }
            return null;
        }
    }

    /**
     * The PageNumber and PageFrame is stored in TLBLine.
     */
    private class TLBLine{
        // 有效位，标记该条数据是否有效
        boolean valid = false;
        char[] page = new char[LINE_SIZE_B/2];
        Long timeStamp = 0l;
        // 数据
        char[] pageFrame = new char[LINE_SIZE_B/2];
        char[] getPageFrame() {
            return this.pageFrame;
        }
        char[] getPage() {
            return this.page;
        }

        void update(char[] npage, char[] npageFrame) {
            valid = true;
            timeStamp = System.currentTimeMillis();
            for (int i=0; i<npage.length; i++) {
                this.page[i] = npage[i];
            }
            // input.length <= this.data.length
            for (int i=0; i<npageFrame.length; i++) {
                this.pageFrame[i] = npageFrame[i];
            }
        }
    }

}
