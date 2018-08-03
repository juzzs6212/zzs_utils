package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by John on 2016/12/21.
 */
public class DateUtil {

    public static Calendar deaCal;

    public static DateUtil ymdHmsDU;

    static {
        ymdHmsDU = new DateUtil("yyyy-MM-dd HH:mm:ss");
        deaCal = Calendar.getInstance();
        deaCal.set(2009, 1, 1);
    }

    private String date_format;
    private ThreadLocal threadlocal;

    public DateUtil(String dateformat) {
        this.date_format = dateformat;
        threadlocal = new ThreadLocal();
    }

    public DateFormat getdateformat() {
        Object obj = threadlocal.get();
        if (obj == null) {
            obj = getobj();
        }
        return (DateFormat) obj;
    }

    private synchronized Object getobj() {
        Object obj = threadlocal.get();
        if (null == obj) {
            obj = new SimpleDateFormat(date_format);
            threadlocal.set(obj);
        }
        return obj;
    }

    public Date parse(String textdate) throws ParseException {
        return getdateformat().parse(textdate);
    }

    public String format(Date dt) {
        return getdateformat().format(dt);
    }

    /**
     * 获取两个时间之间的小时差值
     *
     * @param olddt
     * @param newdt
     * @return
     */
    public static double GetTimeSpanHours(Date olddt, Date newdt) {
        return (newdt.getTime() - olddt.getTime()) / (1000 * 60 * 60);
    }

    /**
     * 获取两个时间之间的天数差值
     *
     * @param olddt
     * @param newdt
     * @return
     */
    public static double GetTimeSpanDays(Date olddt, Date newdt) {
        return (newdt.getTime() - olddt.getTime()) / (1000 * 60 * 60 * 24);
    }

    /**
     * 秒数
     *
     * @param olddt
     * @param newdt
     * @return
     */
    public static double GetTimeSpanSec(Date olddt, Date newdt) {
        return (newdt.getTime() - olddt.getTime()) / 1000;
    }
}
