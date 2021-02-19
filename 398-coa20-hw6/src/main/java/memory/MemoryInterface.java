package memory;

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-07 11:36
 */
public interface MemoryInterface {

    char[] read(String eip, int len);

    void write(String eip, int len, char []data);

}
