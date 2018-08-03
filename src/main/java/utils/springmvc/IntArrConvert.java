package utils.springmvc;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyWin on 2017/12/15 0015.
 */
public class IntArrConvert implements Converter<String, Integer[]> {
    @Override
    public Integer[] convert(String str) {
        try {
            List<Integer> list = new ArrayList<>();
            if (str == null || str.trim().isEmpty())
                return new Integer[0];
            String[] strArr = str.trim().split(",");
            for (String item : strArr) {
                Integer id = Integer.parseInt(item);
                list.add(id);
            }
            Integer[] re = new Integer[list.size()];
            list.toArray(re);
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
