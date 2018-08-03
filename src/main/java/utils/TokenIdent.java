package utils;

/**
 * Created by MyWin on 2018/7/4 0004.
 */
public class TokenIdent {
    private Integer uid;
    private Integer utype;

    public TokenIdent(Integer uid, Integer utype) {
        this.uid = uid;
        this.utype = utype;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getUtype() {
        return utype;
    }

    public void setUtype(Integer utype) {
        this.utype = utype;
    }


}
