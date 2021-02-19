package memory.cacheWriteStrategy;


/**
 * @CreateTime: 2020-11-12 11:39
 */
public class WriteBackStrategy extends WriteStrategy{


    @Override
    public Boolean isWriteBack() {
        return false;
    }
}
