package mcsls.xyz.create_nbt_filter;

import com.mojang.logging.LogUtils;
import mcsls.xyz.create_nbt_filter.model.OriginalRuleList;
import mcsls.xyz.create_nbt_filter.network.RuleUpdater;
import mcsls.xyz.create_nbt_filter.types.Rule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Filter {//过滤器
    private static final Logger LOGGER = LogUtils.getLogger();//获取日志记录器
    private List<Rule> rules;//规则的列表
    private OriginalRuleList originalRuleList;//原始的文件的信息
    private final String RULE_FOLDER_PATH = "./config/createNbtRule";
    private final String RULE_FILE_PATH = "./config/createNbtRule/rule.yml";//规则文件的路径
    private final String BLUEPRINT_FOLDER_PATH = "./schematics/uploaded/";//蓝图文件夹的路径

    private void createFilterFolderIfNotExist()//如果规则文件夹不存在,创建一个规则的文件夹
    {
        File folder = new File(RULE_FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();//文件夹不存在的话就创建一个新的文件夹
        }
    }

    private void createRuleIfNotExist() throws IOException {//如果规则文件不存在就创建从服务器获取规则文件
        File ruleFIle = new File(RULE_FILE_PATH);

        if (!ruleFIle.exists()) { //判断规则文件是否存在
            String data = RuleUpdater.FetchRuleFile();//获取最新的配置文件信息

            LOGGER.info(data);//TODO test
            Files.writeString(ruleFIle.toPath(), data);//将配置文件的信息写出到配置文件中
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

    public void CheckUpdate() throws IOException {//检查文件的版本是否更新了
        String latestVer = RuleUpdater.FetchRuleFile();//获取最新的文件的版本的信息
        if (!latestVer.equals(originalRuleList.getVersion()))//判断当前的规则的文件信息是否是最新的
        {
            LOGGER.info("检测到有更新的规则文件,正在更新中...");

            File file = new File(RULE_FILE_PATH);
            file.delete();//删除旧的文件
            createRuleIfNotExist();            //重新从服务器获取最新的文件的信息

            LOGGER.info("规则文件更新成功");
        }
    }

    public void LoadAllRulesFromFile() throws IOException//从文件中加载所有的规则
    {
        createFilterFolderIfNotExist();//检测规则文件夹是否存在
        createRuleIfNotExist();

        File ruleFile = new File(RULE_FILE_PATH);

        Yaml yaml = new Yaml();
        InputStream in = Files.newInputStream(ruleFile.toPath());
        originalRuleList = yaml.load(in);//读取 yaml 文件

        LOGGER.info("已加载规则文件,版本: " + originalRuleList.getVersion());

//        CheckUpdate();//检查配置文件的更新
    }

    public void FullScan() {//扫描用户上传的所有的文件
        File bluePrintFolder = new File(BLUEPRINT_FOLDER_PATH);
        long startTime = System.currentTimeMillis();

        File[] bluePrintFolderList = bluePrintFolder.listFiles();
        if (bluePrintFolderList == null) {//判断这个文件夹是否为空
            return;
        }

        for (File file : bluePrintFolderList) {//遍历这个文件夹下面的所有的文件
            LOGGER.info("正在扫描 " + file.getName() + " 玩家的蓝图文件.");

            File[] playerFileList = file.listFiles();
            if (playerFileList == null)//判断玩家的蓝图文件是否为空
            {
                continue;
            }

            for (File playerFile : playerFileList) {//遍历这个玩家上传的所有的文件
                if (!VerifyBlueprint(file.getName() + "/" + playerFile.getName())) {//判断用户文件校验是否成功
                    LOGGER.info(Msg.ANSI_RED + "玩家 " + file.getName() + " 上传了异常蓝图: " + playerFile.getName() + Msg.ANSI_RESET);
                }
            }
        }

        LOGGER.info("扫描完成,用时: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public Filter()//构造函数
    {
        rules = new ArrayList<>();
    }
}
