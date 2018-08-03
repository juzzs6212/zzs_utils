package utils.match;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class MatchEngine implements Runnable {
    private MatchedCallback matchedCallback;
    private boolean acitve;
    private HashMap<Integer, LinkedList<MatchItem>[]> sizeItemCache;
    private ReentrantLock sync;
    private Long sleepInvMs = 500L;

    public MatchEngine(MatchedCallback matchedCallback) {
        this.matchedCallback = matchedCallback;
        this.acitve = false;
        this.sizeItemCache = new HashMap<>();
        this.sync = new ReentrantLock();
    }

    public MatchedCallback getMatchedCallback() {
        return matchedCallback;
    }

    public void setMatchedCallback(MatchedCallback matchedCallback) {
        this.matchedCallback = matchedCallback;
    }

    public boolean isAcitve() {
        return acitve;
    }

    public void setAcitve(boolean acitve) {
        this.acitve = acitve;
    }

    public HashMap<Integer, LinkedList<MatchItem>[]> getSizeItemCache() {
        return sizeItemCache;
    }

    public void setSizeItemCache(HashMap<Integer, LinkedList<MatchItem>[]> sizeItemCache) {
        this.sizeItemCache = sizeItemCache;
    }

    public void start() {
        if (acitve)
            return;
        acitve = true;
        new Thread(this).start();
    }

    public void stop() {
        if (!acitve)
            return;
        acitve = false;
    }

    /**
     * 添加消息
     * @param item
     */
    public void addMatchItem(MatchItem item) {
        try {
            if (!item.enable)
                return;
            sync.lock();
            LinkedList<MatchItem>[] listArr;
            if (!sizeItemCache.containsKey(item.expect)) {
                LinkedList[] arr = new LinkedList[item.expect - 1];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = new LinkedList();
                }
                sizeItemCache.put(item.expect, arr);
            }
            listArr = sizeItemCache.get(item.expect);
            listArr[item.size - 1].addLast(item);
        } finally {
            sync.unlock();
        }
    }

    /**
     * 设置有效情况
     * @param item
     * @param enable
     */
    public void setItemEnable(MatchItem item, boolean enable){
        try {
            sync.lock();
            item.enable = enable;
        } finally {
            sync.unlock();
        }
    }

    @Override
    public void run() {
        try {
            while (acitve) {
                List<List<MatchItem>> teams = new LinkedList<>();
                TeamParam param = new TeamParam();
                try {
                    sync.lock();
                    for (Map.Entry<Integer, LinkedList<MatchItem>[]> kv : sizeItemCache.entrySet()) {
                        int except = kv.getKey();
                        LinkedList<MatchItem>[] listArr = kv.getValue();
                        makeTeam(except, listArr, param, teams);
                    }
                    // 去掉无效的
                } finally {
                    sync.unlock();
                }
                // 开始通知
                if (teams != null && null != matchedCallback) {
                    for (List<MatchItem> team : teams) {
                        matchedCallback.match(team);
                    }
                }
                try {
                    Thread.sleep(sleepInvMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            acitve = false;
        }
    }

    public void currDataLog(int key) {
        StringBuilder sb = new StringBuilder();
        sb.append("当前数据内容:\r\n");
        LinkedList<MatchItem>[] listArr = sizeItemCache.get(key);
        if (listArr != null) {
            for (int i = 0; i < listArr.length; i++) {
                sb.append("size:").append(i + 1).append(",cnt:").append(listArr[i].size()).append("\r\n");
            }
        }
        System.out.println(sb.toString());
    }

    /**
     * 组队
     *
     * @param except
     * @param listArr
     * @return
     */
    private void makeTeam(int except, LinkedList<MatchItem>[] listArr, TeamParam param, List<List<MatchItem>> teamList) {
        if (param.sizeDiff == -1) {
            param.sizeDiff = except;
        }
        LinkedList<MatchItem> list = getValueList(param.sizeDiff, param.firstIndex, listArr);
        if (list == null) {
            // 还原回去
            for (MatchItem item : param.team) {
                listArr[item.size - 1].addLast(item);
            }
            param.team.clear();
            if (param.firstIndex > 0) {
                // 如果还有数字继续找小的数字试一下，如果firstIndex找到最小了，就返回失败
                param.firstIndex--;
                param.team = new LinkedList<>();
                param.sizeDiff = except;
                makeTeam(except, listArr, param, teamList);
            } else {
                return;
            }
        } else {
            MatchItem item = list.removeFirst();
            if (param.firstIndex == -1) {
                param.firstIndex = item.size - 1;
            }
            param.sizeDiff -= item.size;
            param.team.addLast(item);
            if (param.sizeDiff == 0) {
                // 此处有一个优化算法是，如果匹配到一个合适的组合，那么，判定一下这个组合可以有多少组
                getTeamByModel(param.team, listArr, teamList);

                teamList.add(param.team);
                param.team = new LinkedList<>();
                param.sizeDiff = except;
                param.firstIndex = -1;
            } else {
                makeTeam(except, listArr, param, teamList);
            }
        }
    }

    private void getTeamByModel(List<MatchItem> model, LinkedList<MatchItem>[] listArr, List<List<MatchItem>> teamList){
        HashMap<Integer,Integer> beiShu = new HashMap<>();
        for (MatchItem si : model){
            if (beiShu.containsKey(si.size)) {
                beiShu.put(si.size, beiShu.get(si.size) + 1);
            } else {
                beiShu.put(si.size, 1);
            }
        }
        int minCnt = -1;
        for (Map.Entry<Integer, Integer> kv : beiShu.entrySet()){
            Integer key = kv.getKey();
            int size = listArr[key - 1].size();
            int n = size / beiShu.get(key);
            if (minCnt == -1) {
                minCnt = n;
            } else {
                if (minCnt > n){
                    minCnt = n;
                }
            }
        }
        if (minCnt > 0){
            // 直接循环取这么多组就行了
            for (int i = 0; i < minCnt; i ++){
                List<MatchItem> temp = new LinkedList<>();
                for (MatchItem si : model){
                    temp.add(listArr[si.size - 1].removeFirst());
                }
                teamList.add(temp);
            }
        }
    }

    private LinkedList<MatchItem> getValueList(int sizeDiff, int beginIndex, LinkedList<MatchItem>[] listArr) {
        // 根据差值来选取一个非空的list
        LinkedList<MatchItem> list = null;
        int index;
        if (sizeDiff >= listArr.length) {
            index = listArr.length - 1;
        } else {
            index = sizeDiff - 1;
        }
        if (beginIndex >= 0) {
            index = Math.min(beginIndex, index);
        }
        while (index >= 0) {
            list = listArr[index];
            // 去掉无效的点
            while (!list.isEmpty()) {
                if (list.getFirst().enable) {
                    return list;
                } else {
                    list.removeFirst();
                }
            }
            index--;
        }
        return null;
    }

    class TeamParam {
        public LinkedList<MatchItem> team = new LinkedList<>();
        public int firstIndex = -1;//从哪个开始匹配的
        public int sizeDiff = -1;
    }

    public static long getIndexMask(int index, boolean flag) {
        long l = 0L;
        index = Math.abs(index) % 32;
        l |= (1L << index);
        if (flag) {
            l = l << 32;
        }
        return l;
    }

    public static void main(String[] args) {
        System.out.println(getIndexMask(24, true));
        MatchEngine matchEngine = new MatchEngine(new MatchedCallbackImpl());
        matchEngine.start();
        for (int i = 0; i < 1000; i ++) {
            MatchItemDef item = new MatchItemDef();
            matchEngine.addMatchItem(item);
        }
        while (true) {
            try {
                matchEngine.currDataLog(11);
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
