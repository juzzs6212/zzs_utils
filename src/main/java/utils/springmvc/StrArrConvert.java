package utils.springmvc;

import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyWin on 2017/12/15 0015.
 */
public class StrArrConvert implements Converter<String, String[]> {
    @Override
    public String[] convert(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<String> list = new ArrayList<>();
            if (str == null || str.trim().isEmpty())
                return new String[0];
            String[] strArr = str.trim().split(",");
            for (String item : strArr) {
                if (!item.isEmpty())
                    list.add(item);
            }
            String[] re = new String[list.size()];
            list.toArray(re);
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
