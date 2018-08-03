package utils.msgEngine;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MsgEngine implements Runnable {
    private Integer corePoolSize;//
    private Integer maximumPoolSize;//
    private Boolean active;//激活标志
    private TreeSet<BaseMsg> msgPool;//消息集合
    private ReentrantLock poolSync;// 集合同步变量
    private ThreadPoolExecutor threadPoolExecutor;//执行线程池
    private Long getMsgMsInv = 10L;//获取消息的时间间隔

    public MsgEngine() {
        corePoolSize = 1;
        maximumPoolSize = 5;
        active = false;
        poolSync = new ReentrantLock();
        msgPool = new TreeSet<>();
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public synchronized void start() {
        if (active)
            return;
        active = true;
        new Thread(this).start();
    }

    public synchronized void stop() {
        if (!active)
            return;
        active = false;
    }

    public synchronized void shutdown() {
        stop();
        threadPoolExecutor.shutdown();
    }

    @Override
    public void run() {
        try {
            while (active) {
                List<BaseMsg> list = getMsg();
                if (list.isEmpty()) {
                    try {
                        Thread.sleep(getMsgMsInv);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (BaseMsg msg : list) {
                        threadPoolExecutor.execute(msg);
                    }
                }
            }
        } finally {
            active = false;
        }
    }

    private List<BaseMsg> getMsg() {
        List<BaseMsg> list = new LinkedList<>();
        try {
            long dt = System.currentTimeMillis();
            poolSync.lock();
            // 获取时间达到了且需要执行的消息
            while (!msgPool.isEmpty()) {
                BaseMsg msg = msgPool.first();
                if (dt >= msg.getRunDt()) {
                    msgPool.pollFirst();
                    if (msg.isEnable()) {
                        list.add(msg);
                    }
                } else {
                    break;
                }
            }
        } finally {
            poolSync.unlock();
        }
        return list;
    }

    public Boolean addMsg(BaseMsg msg) {
        Boolean flag = false;
        try {
            poolSync.lock();
            msgPool.add(msg);
            flag = true;
        } finally {
            poolSync.unlock();
        }
        return flag;
    }

    public Boolean addMsg(List<BaseMsg> msgList) {
        Boolean flag = false;
        try {
            poolSync.lock();
            msgPool.addAll(msgList);
            flag = true;
        } finally {
            poolSync.unlock();
        }
        return flag;
    }
}
