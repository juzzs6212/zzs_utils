package utils.springmvc;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by MyWin on 2017/12/15 0015.
 */
public class DateConvert implements Converter<String, Date> {
    private static final String pattern = "\\d+";

    @Override
    public Date convert(String stringDate) {
        try {
            SimpleDateFormat simpleDateFormat;
            // 首先看看是不是纯数字
            if (Pattern.matches(pattern, stringDate)) {
                // 取出当前的时间的毫秒数根据长度来判断上行的时间是毫秒还是秒
                long currMS = new Date().getTime();
                long dataL = Long.parseLong(stringDate);
                if (dataL >= currMS / 2)
                    return new Date(dataL);
                else
                    return new Date(dataL * 1000);
            } else {
                if (stringDate.length() > 10) {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return simpleDateFormat.parse(stringDate);
                } else {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return simpleDateFormat.parse(String.format("%s 23:59:59", stringDate));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
