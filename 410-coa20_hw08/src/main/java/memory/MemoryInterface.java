package memory;

/**
 * @CreateTime: 2020-11-07 11:36
 */
public interface MemoryInterface {

    char[] read(String eip, int len);

    void write(String eip, int len, char []data);

    void setDisk(DiskInterface disk);

    /**
     * 强制创建一个段描述符，指向指定的物理地址，以便测试用例可以直接修改Disk，
     * 而不用创建一个模拟进程，自上而下进行修改，也不用实现内存分配策略
     * 此方法仅被测试用例使用，而且使用时需小心，此方法不会判断多个段描述符对应的物理存储区间是否重叠
     * @param segSelector
     * @param eip 32-bits
     * @param len
     */
    void alloc_seg_force(int segSelector, String eip, int len, boolean isValid, String disk_base);

    /**
     * 清空段表页表，用于测试用例
     */
    void clear();

    /**
     * 强制使段/页失效，仅用于测试用例
     * @param segNO
     * @param pageNO
     */
    void invalid(int segNO, int pageNO);

    void real_load(String eip, int len);

    String seg_load(int segIndex);

    boolean isValidSegDes(int index);

    char[] getLimitOfSegDes(int index);

    boolean isValidPage(int pageNO);

    String page_load(int segIndex, int pageNO);

    char[] getFrameOfPage(int pageNO);

    char[] getBaseOfSegDes(int index);

}
