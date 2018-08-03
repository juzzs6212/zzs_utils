package utils.match;

import java.util.Random;

public class MatchItemDef extends MatchItem {
    static Random random = new Random();

    public MatchItemDef() {
        this.enable = true;
        this.expect = 11;
        this.size = Math.abs(random.nextInt()) % 7 + 1;
        this.handle = false;
    }
}
