package utils.match;

import java.util.List;

public class MatchedCallbackImpl implements MatchedCallback {
    @Override
    public void match(List<MatchItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("组合:");
        for (MatchItem item : items) {
            sb.append(item.size).append(",");
        }
        System.out.println(sb.toString());
    }
}
