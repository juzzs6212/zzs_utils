package utils;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by MyWin on 2017/12/15 0015.
 */
public class ArrayUtils {
    /**
     * 去重
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T[] heavyArray(T[] array) {
        Set<T> set = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            set.add(array[i]);
        }
        T[] arr = (T[]) new Object[set.size()];
        return set.toArray(arr);
    }
}
