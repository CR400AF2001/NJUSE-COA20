package memory;

import cpu.TLB;
import cpu.alu.ALU;
import transformer.Transformer;

import java.util.*;

/**
 * 内存抽象类
 */
public class Memory implements MemoryInterface{

    /*
     * ------------------------------------------
     *            |  Segment=true | Segment=false
     * ------------------------------------------
     * Page=true  |     段页式    |    不存在
     * ------------------------------------------
     * Page=false |    只有分段   |   实地址模式
     * ------------------------------------------
     *
     * 请实现三种模式下的存储管理方案：
     * 实模式：
     * 		无需管理，该情况下不好判断数据是否已经加载到内存(除非给每个字节建立有效位)，干脆每次都重新读Disk(地址空间不会超过1 MB)
     * 分段：
     * 		最先适应 -> 空间不足则判断总剩余空间是否足够 -> 足够则进行碎片整理，将内存数据压缩
     * 													 -> 不足则采用最近使用算法LRU直到总剩余空间足够 -> 碎片整理
     * 段页：
     * 		如果数据段已经在内存，使用全关联映射+LRU加载物理页框；如果数据段不在内存，先按照分段模式进行管理，分配的段长度为数据段包含的总物理页框数/2，再将物理页框加载到内存
     */
    public static boolean SEGMENT = false;

    public static boolean PAGE = false;

    public static int MEM_SIZE_B = 32 * 1024 * 1024;      // 主存大小 32 MB

    public static int PAGE_SIZE_B = 1 * 1024;      // 页大小 1 KB

    Transformer t = new Transformer();

    private static char[] memory = new char[MEM_SIZE_B];

    private static ArrayList<SegDescriptor> segTbl = new ArrayList<>();

    private static PageItem[] pageTbl = new PageItem[Disk.DISK_SIZE_B / Memory.PAGE_SIZE_B]; // 页表大小为2^16  64K

    private static ReversedPageItem[] reversedPageTbl = new ReversedPageItem[Memory.MEM_SIZE_B / Memory.PAGE_SIZE_B]; // 反向页表大小为2^15   32K

    private static Memory memoryInstance = new Memory();

    private Memory() {}

    private HashMap<String, String> stack = new HashMap<>();

    private Transformer transformer = new Transformer();

    public static Memory getMemory() {
        return memoryInstance;
    }

    public void pushStack(String esp, String value){
        stack.put(transformer.binaryToInt(esp), value);
    }

    public String topOfStack(String esp){
        return stack.get(transformer.binaryToInt(esp));
    }

    public DiskInterface disk = Disk.getDisk();

    TLB tlb = TLB.getTLB();

    @Override
    public void setDisk(DiskInterface disk) {
        this.disk = disk;
    }

    /**
     * 分段开启的情况下，read方法不允许一次性读取两个段的内容，但是可以一次性读取单个段内多页的内容
     * 注意， read方法应该在load方法被调用之后调用，即read方法的目标页(如果开启分页)都是合法的
     * @param eip
     * @param len
     * @return
     */
    public char[] read(String eip, int len){
        boolean isReadValid = false;
        if (!SEGMENT) {
            isReadValid = true; // 实模式下允许任意位置随机读取
        } else {
            int readFrom = Integer.parseInt(t.binaryToInt("0" + eip));
            int readTo = readFrom + len;
            for (SegDescriptor sd:segTbl) {
                if (sd.validBit) {
                    if (chars2int(sd.base) <= readFrom && chars2int(sd.base) + chars2int(sd.limit) >= readTo) {
                        isReadValid = true;
                        // 更新LRU时间戳
                        sd.updateTimeStamp();
                        if (PAGE) {
                            int fromPage = readFrom / PAGE_SIZE_B;
                            int toPage = readTo / PAGE_SIZE_B-1;
                            for (int i=fromPage; i<=toPage; i++) {
                                if (!(reversedPageTbl( i ).isValid)) {
                                    throw new SecurityException("非法访问页");
                                } else {
                                    reversedPageTbl[i].updateTimeStamp();
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        // 读取数据
        if (isReadValid) {
            char[] data = new char[len];
            int base = Integer.parseInt(new Transformer().binaryToInt(eip));
            for (int ptr=0; ptr<len; ptr++) {
                data[ptr] = memory[base + ptr];
            }
            return data;
        } else {
            throw new SecurityException("不允许同时读取多个段");
        }
    }


    private char[] read_for_manage(String eip, int len){
        char[] data = new char[len];
        for (int ptr=0; ptr<len; ptr++) {
            data[ptr] = memory[Integer.parseInt(new Transformer().binaryToInt(eip)) + ptr];
        }
        return data;
    }


    /**
     * 实模式下从磁盘中加载数据
     * @param eip 实模式下，内存0-32MB地址对应磁盘0-32MB地址
     * @param len 数据段长度
     */
    public void real_load(String eip, int len){
        //TODO 从磁盘中加载数据
        char[] data = disk.read(eip, len);
        write( eip,len, data );
    }


    /**
     * 段式存储模式下，从磁盘中加载数据.段页式存储中，不用从磁盘中加载数据
     * @param segIndex 段号
     * @return 该段的内存基址
     */
    public String seg_load(int segIndex) {
        //TODO
        SegDescriptor targetSeg = segTbl.get( segIndex );
        String eip = String.valueOf(targetSeg.getDisk());
        int len = Integer.parseInt(t.binaryToInt("0" + String.valueOf(targetSeg.getLimit())));

        //TODO 使用 removeSegByLRU 和 occupiedSize 在内存中空出一块足够且连续的空间加载段
        // code here
        while(MEM_SIZE_B - occupiedSize() < len){
            removeSegByLRU();
        }


        //TODO 使用 fit 从磁盘中加载数据
        // 段页式时不需要从磁盘中加载数据
        if(Memory.PAGE){
            // Code here
            // You don't need to load data from disk
            fit(segIndex, null, len);
        }else{
            // Code here
            char[] data = disk.read(eip, len);
            fit(segIndex, data, len);
        }

        return String.valueOf(  targetSeg.getBase());
    }


    /**
     * 段页式存储下，从磁盘中加载数据
     * @param segIndex 段号
     * @param pageNO 虚拟页号
     * @return 该页的起始地址32-bits
     */
    public String page_load(int segIndex, int pageNO) {
        //TODO
        // 首先，你需要把页号对应的数据加载到内存中
        // 第二，你需要从段中表达的那些页框里选出一个和虚页号对应起来，如果全都映射到了虚页，那你需要使用我们提供的removePageByLRU来获取一个有效页
        // 第三，设置好反向页表和页表
        // 最后，添加TLB，在往TLB里放入虚页：页框项的时候，需要把数据也加载到内存之中，否则会出现TLB命中，页表命中，Cache访问内存物理地址，但是内存却没有这个数据的尴尬情况。

        SegDescriptor seg = segTbl.get(segIndex);
        int segLimit = Integer.parseInt(t.binaryToInt(String.valueOf(seg.limit))) / Memory.PAGE_SIZE_B;
        int min = Math.min(segLimit, tlb.getTlbSize() / 2 - 1);
        char[] data = Disk.getDisk().read(t.intToBinary(String.valueOf(pageNO * PAGE_SIZE_B)), (min + 1) * PAGE_SIZE_B);
        int base = Integer.parseInt(t.binaryToInt(String.valueOf(seg.getBase())));
        int limit = Integer.parseInt(t.binaryToInt(String.valueOf(seg.getLimit())));
        int start = base / PAGE_SIZE_B;
        int end = ((base + limit) / PAGE_SIZE_B) - 1;
        String frameAddr = "";

        for(int j = min; j >= 0; --j){
            int reversePageNo = 0;
            for(int i = start; i <= end; i++){
                if(!reversedPageTbl(i).isValid){
                    reversePageNo = i;
                    break;
                }
                if(i == end){
                    reversePageNo = removePageByLRU(start, end);
                    break;
                }
            }
            frameAddr = String.valueOf(t.intToBinary(String.valueOf(reversePageNo * PAGE_SIZE_B)));
            write(frameAddr, PAGE_SIZE_B, Arrays.copyOfRange(data, j * PAGE_SIZE_B, (j + 1) * PAGE_SIZE_B));
            ReversedPageItem reversedPageItem = reversedPageTbl(reversePageNo);
            reversedPageItem.isValid = true;
            reversedPageItem.vPageNO = pageNO + j;
            reversedPageItem.updateTimeStamp();
            PageItem pageItem = pageTbl(pageNO + j);
            pageItem.isInMem = true;
            pageItem.setFrameAddr(frameAddr.toCharArray());
            tlb.writeTLB(t.intToBinary(String.valueOf(pageNO + j)).toCharArray(), frameAddr.());
        }

        return frameAddr;
    }

    /**
     * 根据LRU算法移除段内的一个页
     * @param startFrame 该段第一个物理页号
     * @param endFrame 该段最后一个物理页号
     * @return 被移除的页的物理页号
     */
    private int removePageByLRU(int startFrame, int endFrame) {
        long minTime = Long.MAX_VALUE;
        int minIndex = startFrame;

        for(int i=startFrame+1;i<=endFrame;i++){
            if(reversedPageTbl[i].getTimeStamp() < minTime){
                minTime = reversedPageTbl[i].getTimeStamp();
                minIndex = i;
            }
        }

        reversedPageTbl[minIndex].isValid = false;
        pageTbl( reversedPageTbl[minIndex].vPageNO ).setInMem( false);
        int row = tlb.isMatch(reversedPageTbl[minIndex].vPageNO);
        if(row != -1){
            tlb.invalid(row);
        }
        return minIndex;
    }

    @Override
    public void write(String eip, int len, char []data){
        // 通知Cache缓存失效
        // 本作业只要求读数据，不要求写数据，因此不存在程序修改数据导致Cache修改 -> Mem修改 -> Disk修改等一系列write back/write through操作，
        //     write方法只用于测试用例中的下层存储修改数据导致上层存储数据失效，Disk.write同理
        Cache.getCache().invalid(eip, len);
        // 更新数据
        int start = Integer.parseInt(new Transformer().binaryToInt(eip));
        for (int ptr=0; ptr<len; ptr++) {
            memory[start + ptr] = data[ptr];
        }
    }

    /**
     * 强制创建一个段描述符，指向指定的物理地址，以便测试用例可以直接修改Disk，
     * 而不用创建一个模拟进程，自上而下进行修改，也不用实现内存分配策略
     * 此方法仅被测试用例使用，而且使用时需小心，此方法不会判断多个段描述符对应的物理存储区间是否重叠
     * @param segSelector
     * @param eip 32-bits
     * @param len
     */
    public void alloc_seg_force(int segSelector, String eip, int len, boolean isValid, String disk_base) {
        SegDescriptor sd = new SegDescriptor();
        Transformer t = new Transformer();
        sd.setDisk(disk_base.toCharArray());
        sd.setBase(eip.toCharArray());
        sd.setLimit(t.intToBinary(String.valueOf(len)).substring(1, 32).toCharArray());
        sd.setValidBit(isValid);
        Memory.segTbl.add(segSelector, sd);
    }

    public void alloc_page_force(int vPageNO, String eip, int pageNO) {
        pageTbl(vPageNO).setInMem(true);
        pageTbl(vPageNO).setFrameAddr(eip.toCharArray());
        reversedPageTbl(pageNO).isValid = true;
        reversedPageTbl(pageNO).vPageNO = vPageNO;
    }

    public void clear() {
        segTbl = new ArrayList<>();
        for (PageItem pItem:pageTbl) {
            if (pItem != null) {
                pItem.isInMem = false;
            }
        }
        stack.clear();
    }



    /**
     * 根据segment descriptor的索引返回该SegDescriptor的limit
     * @param index
     * @return 20-bits
     */
    public char[] getLimitOfSegDes(int index) {
        if(index<0 || index>segTbl.size()) return null;
        return segTbl.get( index ).getLimit();
    }


    /**
     * 根据segment descriptor的索引返回该SegDescriptor的base
     * @param index
     * @return 32-bits
     */
    public char[] getBaseOfSegDes(int index) {
        if(index<0 || index>segTbl.size()) return null;
        return segTbl.get( index ).getBase();
    }


    /**
     * 根据segment descriptor的索引返回该SegDescriptor是否有效
     * @param index
     * @return boolean
     */
    public boolean isValidSegDes(int index) {
        SegDescriptor segDescriptor = segTbl.get( index );
        return segDescriptor.isValidBit();
    }

    public void invalid(int segNO, int pageNO) {
        if (segNO >= 0) {
            segTbl.get(segNO).validBit = false;
        }
        if (Memory.PAGE) {
            if (pageNO >= 0) {
                pageTbl(pageNO).setInMem(false);
            }
        }
    }


    /**
     * 根据page索引返回该page是否在内存中
     * @param pageNO 范围是
     * @return boolean
     */
    public boolean isValidPage(int pageNO) {
        return pageTbl(pageNO).isInMem();
    }


    /**
     * 根据虚页页号返回该页的物理页框号
     * @param pageNO 线性地址高20位的int形式
     * @return 20-bits
     */
    public char[] getFrameOfPage(int pageNO) {
        return pageTbl(pageNO).getFrameAddr();
    }


    /**
     * 获取内存中已占用的空间大小
     * @return
     */
    private int occupiedSize() {
        int total = 0;
        for(SegDescriptor segDescriptor: segTbl){
            if(segDescriptor.isValidBit()){
                int size = chars2int( segDescriptor.getLimit());
                total += size;
            }
        }
        return total;
    }


    /**
     * 最先适应算法
     * @param index 要考虑的segDescriptor
     * @param data 要存入的数据
     * @param len data的长度
     * @return
     */
    public void fit(int index, char[] data, int len) {
        List<AbstractMap.SimpleEntry<Integer, Integer>> occupiedSegs = new ArrayList<>(  );   // 存储每个被占用的内存空间的segDescriptor的base与index

        for(SegDescriptor segDes: segTbl){
            if(segDes.isValidBit()){    // 已经被占用
                int base = chars2int( segDes.getBase() );
				AbstractMap.SimpleEntry<Integer, Integer> pair = new AbstractMap.SimpleEntry<>( base, segTbl.indexOf( segDes ) );
                occupiedSegs.add( pair );
            }
        }

        Collections.sort( occupiedSegs, new Comparator<AbstractMap.SimpleEntry<Integer, Integer>>() {  // 按起始位置从小到大排序
            @Override
            public int compare(AbstractMap.SimpleEntry<Integer, Integer> p1, AbstractMap.SimpleEntry<Integer, Integer> p2) {
                return p1.getKey()-p2.getKey();
            }
        } );

        int current = 0;    // 当前地址前的空间是紧凑的
        int totalFree;   // 已经找到的空闲空间
//        System.out.println("Start Fit:" + occupiedSegs.size());
        for(AbstractMap.SimpleEntry<Integer,Integer> occupied: occupiedSegs){
            int base = occupied.getKey();
            totalFree = base-current;
            if(totalFree >= len) {
                break;
            }
            SegDescriptor segDes = segTbl.get( occupied.getValue() );
            int limit = chars2int( segDes.getLimit());
            if(occupied.getKey()!=current){    // 整理碎片
                char[] segData = read_for_manage( String.valueOf( segDes.getBase() ), limit);
                write( String.valueOf( int2chars( current ) ), limit, segData );
                segDes.setBase( t.intToBinary( String.valueOf( current )).toCharArray());
            }

            current += limit;

        }

        SegDescriptor targetSegDes = segTbl.get( index );
        targetSegDes.setBase( int2chars( current ) );
        targetSegDes.setValidBit( true );
        targetSegDes.updateTimeStamp();
        // 分段模式下载入内存，段页模式下不载入内存
        if(!Memory.PAGE){
            write( String.valueOf( int2chars( current )) , chars2int( targetSegDes.getLimit()) ,data);
        }

    }

    // 返回32位
    private char[] int2chars(int num){
        return t.intToBinary( num+"" ).toCharArray();

    }

    private int chars2int(char[] chars){
        return Integer.parseInt( t.binaryToInt( String.valueOf( chars ) ) );
    }

    /**
     * 按照LRU算法将内存中的某个段置为无效
     */
    public void removeSegByLRU() {
        long minTime = Long.MAX_VALUE;
        int minIndex = 0;

        for(SegDescriptor segDescriptor: segTbl){

            if(segDescriptor.isValidBit()){
                if(segDescriptor.getTimeStamp() < minTime){
                    minTime = segDescriptor.getTimeStamp();
                    minIndex = segTbl.indexOf( segDescriptor );
                }
            }
        }

        segTbl.get( minIndex ).setValidBit( false );
    }

    PageItem pageTbl(int index) {
        if (pageTbl[index] == null) {
            pageTbl[index] = new PageItem();
            return pageTbl[index];
        } else {
            return pageTbl[index];
        }
    }


    ReversedPageItem reversedPageTbl(int index){
        if(reversedPageTbl[index] == null){
            reversedPageTbl[index] = new ReversedPageItem();
            return reversedPageTbl[index] ;
        }else{
            return reversedPageTbl[index];
        }
    }


    /**
     * 理论上应该为Memory分配出一定的系统保留空间用于存放段表和页表，并计算出每个段表和页表所占的字节长度，通过地址访问
     * 不过考虑到Java特性，在此作业的实现中为了简化难度，全部的32M内存都用于存放数据(代码段)，段表和页表直接用独立的数据结构表示，不占用"内存空间"
     * 除此之外，理论上每个进程都会有对应的段表和页表，作业中则假设系统只有一个进程，因此段表和页表也只有一个，不需要再根据进程号选择相应的表
     * 段选择符理论长度为64-bits，包括32-bits基地址和20-bits的限长(1 MB)，为了测试用例填充内存方便，未被使用的11-bits数据被添加到限长，即作业中限长为31-bits
     */
    private class SegDescriptor {
        // 段基址在缺段中断发生时可能会产生变化，内存重新为段分配内存基址
        private char[] base = new char[32];  // 32位基地址

        private char[] limit = new char[31]; // 31位限长，表示段在内存中的长度

        private boolean validBit = false;    // 有效位,为true表示被占用（段已在内存中），为false表示空闲（不在内存中）

        private long timeStamp = 0l;

        // 段在物理磁盘中的存储位置，真实段描述符里不包含此字段，本作业规定，段在磁盘中连续存储，并且磁盘中的存储位置不会发生变化
        private char[] disk_base = new char[32];

        public SegDescriptor () {
            timeStamp = System.currentTimeMillis();
        }

        public char[] getBase() {
            return base;
        }

        public void setBase(char[] base) {
            this.base = base;
        }

        public char[] getDisk() {
            return disk_base;
        }

        public void setDisk(char[] base) {
            this.disk_base = base;
        }

        public char[] getLimit() {
            return limit;
        }

        public void setLimit(char[] limit) {
            this.limit = limit;
        }

        public boolean isValidBit() {
            return validBit;
        }

        public void setValidBit(boolean validBit) {
            this.validBit = validBit;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void updateTimeStamp() {
            this.timeStamp = System.currentTimeMillis();
        }
    }


    /**
     * 页表项为长度为20-bits的页框号
     */
    private class PageItem {

        private char[] frameAddr;

        private boolean isInMem = false;

        public char[] getFrameAddr() {
            return frameAddr;
        }

        public void setFrameAddr(char[] frameAddr) {
            this.frameAddr = frameAddr;
        }

        public boolean isInMem() {
            return isInMem;
        }

        public void setInMem(boolean inMem) {
            isInMem = inMem;
        }

    }

    /**
     * 反向页表 32 * 1024 * 1024 / 1 * 1024 = 32 * 1024 = 32 KB 个反向页表项
     */
    private class ReversedPageItem {

        private boolean isValid = false;    // false表示不在内存，true表示在内存中

        private int vPageNO = -1;           // 虚页页号

        private long timeStamp = System.currentTimeMillis();

        public long getTimeStamp(){
            return this.timeStamp;
        }

        public void updateTimeStamp(){
            this.timeStamp = System.currentTimeMillis();
        }

    }


}