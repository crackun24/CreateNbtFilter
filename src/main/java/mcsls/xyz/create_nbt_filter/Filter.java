package mcsls.xyz.create_nbt_filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mojang.logging.LogUtils;
import mcsls.xyz.create_nbt_filter.model.OriginalRule;
import mcsls.xyz.create_nbt_filter.model.OriginalRuleList;
import mcsls.xyz.create_nbt_filter.network.RuleUpdater;
import mcsls.xyz.create_nbt_filter.types.JudgeType;
import mcsls.xyz.create_nbt_filter.types.Rule;
import mcsls.xyz.create_nbt_filter.types.RuleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filter {//过滤器
    private static final Logger LOGGER = LogUtils.getLogger();//获取日志记录器

    private List<Rule> rules;//规则的列表
    private OriginalRuleList originalRuleList;//原始的文件的信息

    private final String RULE_FOLDER_PATH = "./config/createNbtRule";
    private final String RULE_FILE_PATH = "./config/createNbtRule/rule.yml";//规则文件的路径
    private final String BLUEPRINT_FOLDER_PATH = "./schematics/uploaded/";//蓝图文件夹的路径
    private final String BLOCK_PATH = "blocks.nbt.id";//设置方块的路径

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

            Files.writeString(ruleFIle.toPath(), data);//将配置文件的信息写出到配置文件中
        }
    }

    private Boolean verifyNBTData(CompoundTag nbt_data)//校验 NBT 数据
    {
        for (Rule rule : rules)//遍历规则列表
        {
            if (rule.IsBlueprintMatch(nbt_data))//校验蓝图是否匹配规则 (如果匹配规则话就是说明含有了非法内容)
            {
                LOGGER.info("违反规则: " + Msg.ANSI_RED + rule.getName() + Msg.ANSI_RESET);//TODO test
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
        String latestVer = RuleUpdater.FetchVersion();//获取最新的文件的版本的信息

        LOGGER.info("latest ver: " + latestVer + " current: " + originalRuleList.getVersion());
        if (!latestVer.equals(originalRuleList.getVersion()))//判断当前的规则的文件信息是否是最新的
        {
            LOGGER.info(Msg.ANSI_GREEN + "检测到有更新的规则文件,正在更新中..." + Msg.ANSI_RESET);

            File file = new File(RULE_FILE_PATH);
            file.delete();//删除旧的文件
            createRuleIfNotExist();            //重新从服务器获取最新的文件的信息

            LoadAllRulesFromFile();//重新加载配置文件
            LOGGER.info(Msg.ANSI_GREEN + "规则文件更新成功" + Msg.ANSI_RESET);
            return;
        }

        LOGGER.info(Msg.ANSI_BLUE + "没有更新,当前的规则文件是最新的版本" + Msg.ANSI_RESET);
    }

    public void LoadAllRulesFromFile() throws IOException//从文件中加载所有的规则
    {
        createFilterFolderIfNotExist();//检测规则文件夹是否存在
        createRuleIfNotExist();

        File ruleFile = new File(RULE_FILE_PATH);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        originalRuleList = mapper.readValue(ruleFile, OriginalRuleList.class);//加载原始的数据列表

        for (OriginalRule rule : originalRuleList.getRules()) {//遍历规则列表
            ParseRuleFromOriginalRule(rule);//解析原始的规则
        }


        LOGGER.info(Msg.ANSI_GREEN + "已加载规则文件,版本: " + originalRuleList.getVersion() + Msg.ANSI_RESET);

        CheckUpdate();//检查配置文件的更新
    }

    public void FullScan() throws Exception {//扫描用户上传的所有的文件
        Map<String, Boolean> cache = new HashMap<String, Boolean>();//文件的哈希值的缓存
        int invalidCount = 0;//无效蓝图的计数

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
                boolean isValid = false;//是否是异常的蓝图
                if (cache.containsKey(FilterUtil.CalculateHash(playerFile, "MD5")))//判断是否已经扫描过了
                {
                    isValid = cache.get(FilterUtil.CalculateHash(playerFile, "MD5"));
                } else {
                    isValid = VerifyBlueprint(file.getName() + "/" + playerFile.getName());
                    cache.put(FilterUtil.CalculateHash(playerFile, "MD5"), isValid);//插入到缓存中
                }

                if (!isValid) {//判断用户的蓝图是否正常
                    LOGGER.info(Msg.ANSI_RED + "玩家 " + file.getName() + " 上传了异常蓝图: " + playerFile.getName() + Msg.ANSI_RESET);
                    invalidCount++;
                }

            }
        }

        LOGGER.info(String.format("扫描完成,用时 %d ms,总共 %d 份异常蓝图.", System.currentTimeMillis() - startTime, invalidCount));
    }

    public void ParseRuleFromOriginalRule(OriginalRule originalRule) {

        if (originalRule.univariate != null) {
            for (String key : originalRule.univariate.keySet()) {//遍历所有的键

                if (originalRule.univariate.get(key) instanceof List<?> list)//判断是不是数组类型的
                {
                    List<String> rangeList = list.stream()
                            .map(String::valueOf)  // 转成 int 范围
                            .toList();

                    Rule rule = new Rule(originalRule.getName() + "univariate",//重新设置这个规则的名字
                            RuleType.RANGE,//如果是数组的话,就使用范围的匹配
                            JudgeType.IF_CONTAIN, //使用包含的方法去确认蓝图
                            BLOCK_PATH, //设置为方块的路径
                            key,//单独设置一个规则,过滤路径
                            new ArrayList<String>(List.of(originalRule.getBlock())),
                            rangeList);

                    LOGGER.info("添加范围规则: " + rule.getName());//TODO test
                    rules.add(rule);//添加到规则列表中

                } else {//如果是其他的类型的
                    Rule rule = new Rule(originalRule.getName() + "univariate",//重新设置这个规则的名字
                            RuleType.NOT_EQUAL,//如果不等于目标的值的话,就是非法的蓝图
                            JudgeType.IF_CONTAIN, //判断的类型也是使用数组
                            BLOCK_PATH, //设置为方块的路径
                            key,//单独设置一个规则,过滤路径
                            List.of(originalRule.getBlock()),//对应的方块的值
                            List.of(String.valueOf(originalRule.univariate.get(key))));//设置过滤的路径


                    LOGGER.info("添加值匹配规则: " + rule.getName());//TODO test
                    rules.add(rule);//添加到规则列表中
                }

            }
        }

        if (originalRule.redundant != null) {//判断是否是路径过滤
            for (String key : originalRule.redundant.keySet())//遍历键
            {
                Rule rule = new Rule(originalRule.getName() + "redundant",
                        RuleType.CONTAIN, JudgeType.IF_CONTAIN,
                        BLOCK_PATH,
                        key + "." + originalRule.redundant.get(key),
                        List.of(originalRule.getBlock()), new ArrayList<>());

                LOGGER.info("添加路径包含规则: " + rule.getName());//TODO test
                rules.add(rule);//添加到规则列表中
            }
        }
    }

    public Filter()//构造函数
    {
        rules = new ArrayList<>();
    }
}
