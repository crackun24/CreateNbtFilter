package mcsls.xyz.create_nbt_filter.network;

import com.google.gson.Gson;
import mcsls.xyz.create_nbt_filter.Filter;
import mcsls.xyz.create_nbt_filter.FilterUtil;
import mcsls.xyz.create_nbt_filter.Msg;
import mcsls.xyz.create_nbt_filter.model.FetchRuleResp;
import mcsls.xyz.create_nbt_filter.model.FetchVersionResp;

import java.io.IOException;

import static net.minecraft.core.RegistryAccess.LOGGER;

public class RuleUpdater extends Thread {//规则文件的更新器
    private Filter filter;
    public static String API_ROOT = "https://api.mcsls.xyz";//接口的根地址

    public static String FetchVersion() throws IOException {//获取配置文件的版本信息
        String urlStr = API_ROOT + "/nbt_filter/get_latest_version";//拼接出请求的路径

        String body = FilterUtil.SendHttpReq(urlStr);//发送 http 请求

        Gson gson = new Gson();
        FetchVersionResp resp = gson.fromJson(body, FetchVersionResp.class);//解析请求
        if (resp.code != 0) {//判断错误代码是否为 0
            throw new RuntimeException("Could not fetch version");
        }

        return resp.version;//返回版本信息
    }

    public static String FetchRuleFile() throws IOException {//获取配置文件
        String urlStr = API_ROOT + "/nbt_filter/get_latest_rule";
        String body = FilterUtil.SendHttpReq(urlStr);

        Gson gson = new Gson();
        FetchRuleResp resp = gson.fromJson(body, FetchRuleResp.class);//解析请求
        if (resp.code != 0) {//判断请求是否成功
            throw new RuntimeException("Could not fetch rule file");
        }

        return resp.data;
    }

    public RuleUpdater(Filter filter) {//构造函数
        this.filter = filter;
    }

    @Override
    public void run() {//启动规则文件更新线程
        while (true) {
            try {
                sleep(43200000);
                filter.CheckUpdate();//检查更新
            } catch (Exception e) {
                LOGGER.info(Msg.ANSI_RED + "无法获取规则文件信息,稍后将会自动重试" + Msg.ANSI_RESET);
                e.printStackTrace();
            }
        }
    }
}
