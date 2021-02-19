package memory;


/**
 * @CreateTime: 2020-11-27 21:18
 */
public interface DiskInterface {

    /**
     * 读磁盘
     * @param eip 32位地址
     * @param len 数据长度
     * @return 磁盘数据
     */
    char[] read(String eip, int len);

    /**
     * 写磁盘
     * @param eip 32位地址
     * @param len 数据长度
     * @param data 数据
     */
    void write(String eip, int len, char[] data);

}
