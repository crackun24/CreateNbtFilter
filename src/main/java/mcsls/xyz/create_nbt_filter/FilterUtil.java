package mcsls.xyz.create_nbt_filter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;

public class FilterUtil {
    public static boolean IsValueNotInRange(List<Integer> list, int target)//TODO 判断一个值是不是不在范围里面
    {
        if (target < list.get(0) || target > list.get(1)) {
            return true;
        }
        return false;
    }

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

    public static String BytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // 小写十六进制
        }
        return sb.toString();
    }

    public static String CalculateHash(File file, String algorithm) throws Exception {//计算一个文件的哈希值
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192]; // 8KB缓冲
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        return BytesToHex(hashBytes);
    }
}
