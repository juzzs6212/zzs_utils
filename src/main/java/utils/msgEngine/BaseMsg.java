package utils.msgEngine;

/**
 * 基本消息对象
 * 1、可以拿到一个特征值，以达到同类消息分流的效果
 * 2、可以拿到一个应该要执行的时间点，有序集合中是用这个时间进行排序，以便能快速找到应该执行的消息
 * 3、Enable，决定该消息是否需要执行
 */
public abstract class BaseMsg implements Runnable, Comparable<BaseMsg> {
    private long runDt;
    private boolean enable;
    private int feature;

    @Override
    public int compareTo(BaseMsg o) {
        return (int) (runDt - o.runDt);
    }

    public long getRunDt() {
        return runDt;
    }

    public void setRunDt(long runDt) {
        this.runDt = runDt;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getFeature() {
        return feature;
    }

    public void setFeature(int feature) {
        this.feature = feature;
    }
}
