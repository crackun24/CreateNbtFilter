package mcsls.xyz.create_nbt_filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static String SendHttpReq(String urlStr) throws IOException {
        URL url = new URL(urlStr);//请求的路径
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder res = new StringBuilder();
        if (conn.getResponseCode() == 200)//判断请求是否成功
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {//读取所有的请求的信息
                res.append(line);
            }
            in.close();
        } else {
            conn.disconnect();
            throw new RuntimeException("Could not send http request,code: " + conn.getResponseCode());
        }

        conn.disconnect();

        return res.toString();
    }
}
