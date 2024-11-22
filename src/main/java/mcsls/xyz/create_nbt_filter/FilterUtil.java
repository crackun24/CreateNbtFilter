package mcsls.xyz.create_nbt_filter;

import java.util.List;

public class FilterUtil {
    public static boolean IsStringListContainTarget(List<String> list, String target)//TODO 判断一个字符串数组里面是否包含字符串
    {
        for (String temp : list) {
            if (temp.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
