package utils;

/**
 * Created by MyWin on 2017/12/13 0013.
 */
public class RspData<T> {
    private T data;
    private Integer code;
    private String msg;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
        this.msg = code.toString();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
