package mcsls.xyz.create_nbt_filter;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import mcsls.xyz.create_nbt_filter.types.Rule;
import mcsls.xyz.create_nbt_filter.types.RuleJson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Filter {//过滤器
    private static final Logger LOGGER = LogUtils.getLogger();//获取日志记录器
    private List<Rule> rules;//规则的列表
    private final String RULE_FOLDER_PATH = "./config/createNbtRule";
    private final String BLUEPRINT_FOLDER_PATH = "./schematics/uploaded/";//蓝图文件加的路径

    private void createFilterFolderIfNotExist()//如果规则文件夹不存在,创建一个规则的文件夹
    {
        File folder = new File(RULE_FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();//文件夹不存在的话就创建一个新的文件夹
        }
    }

    private Boolean verifyNBTData(CompoundTag nbt_data)//校验 NBT 数据
    {
        for (Rule rule : rules)//遍历规则列表
        {
            if (rule.IsBlueprintMatch(nbt_data))//校验蓝图是否匹配规则 (如果匹配规则话就是说明含有了非法内容)
            {
                return false;
            }
        }
        return true;
    }

    public Boolean VerifyBlueprint(String blueprintId)//校验蓝图
    {
        String nbt_file_path = BLUEPRINT_FOLDER_PATH + blueprintId;

        File nbt_file = new File(nbt_file_path);//构造蓝图的 NBT 文件的路径
        try {
            FileInputStream in = new FileInputStream(nbt_file);
            CompoundTag nbt_data = NbtIo.readCompressed(in);//读取 NBT 数据
            return verifyNBTData(nbt_data);//返回 NBT 的校验结果
        } catch (IOException e) {
            LOGGER.info(Msg.ANSI_RED + "无法解析蓝图文件: " + nbt_file_path + Msg.ANSI_RESET);
            e.printStackTrace();
            return false;
        }
    }

    public void LoadAllRulesFromFiles() throws FileNotFoundException//从文件中加载所有的规则
    {
        createFilterFolderIfNotExist();//检测规则文件夹是否存在
        File folder = new File(RULE_FOLDER_PATH);//规则的文件夹
        File[] files = folder.listFiles();//获取所有的文件

        if (files != null)//判断获取到的文件列表是否为空
        {
            for (File file : files)//遍历所有的规则的文件
            {
                Gson gson = new Gson();// json 解析对象
                FileReader reader = new FileReader(file);
                RuleJson rule_data = gson.fromJson(reader, RuleJson.class);//反序列化 json 信息

                LOGGER.info("添加规则: " + Msg.ANSI_GREEN + rule_data.name + Msg.ANSI_RESET + " 进入规则表中.");
                rules.add(rule_data.ToRule());//添加进规则列表中
            }
        }
    }

    public Filter()//构造函数
    {
        rules = new ArrayList<>();
    }
}
