package memory.cacheWriteStrategy;

/**
 * @CreateTime: 2020-11-12 15:48
 */
public class WriteThroughStrategy extends WriteStrategy{

    @Override
    public Boolean isWriteBack() {
        return true;
    }
}

