package mcsls.xyz.create_nbt_filter.types;

import com.mojang.logging.LogUtils;
import mcsls.xyz.create_nbt_filter.FilterUtil;
import mcsls.xyz.create_nbt_filter.Msg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

enum RuleType {
    CONTAIN,//如果包含了指定的键,这个蓝图则为非法的蓝图
    NOT_CONTAIN,//如果没有包含指定的键,这个蓝图就是合法的蓝图
    EQUAL, //NBT 标签下的路径必须和 Rule Value 相等
    NOT_EQUAL //NBT 标签下的路径必须和 Rule Value 不相等
}

enum JudgeType {//判断模式
    IF_CONTAIN,//如果包含了值1的路径
    IF_NOT_CONTAIN,//如果没有包含值1的路径
    RANGE//值1的范围
}

public class Rule {//规则
    private static final Logger LOGGER = LogUtils.getLogger();//获取日志记录器
    private String name;//规则的名字
    private RuleType type;//规则的类型
    private JudgeType judgeType;//判断的类型

    private List<String> key1;//规则的键1 无论是什么类型,统一转换为 String 进行比较
    private List<String> key2;//规则的键2 无论是什么类型,统一转换为 String 进行比较

    private List<String> value2;//规则的值2 无论是什么类型,统一转换为 String 进行比较
    private List<String> value1;//规则的值1 无论是什么类型,统一转换为 String 进行比较


    private boolean containKey(int search_index, CompoundTag data, List<String> nodes_list) {
//        LOGGER.info(Msg.ANSI_RED + "当前的索引:" + Integer.toString((search_index)) + Msg.ANSI_RESET);//FIXME test delete

        String search_key = nodes_list.get(search_index);
//        LOGGER.info(Msg.ANSI_RED + "正在检测键: " + search_key + "索引: " + search_index + Msg.ANSI_RESET);//TODO test

        if ((nodes_list.size() - 1) <= (search_index)) {//判断是否已经到了最后一个元素了
            return true;//如果是最后的一个元素的话,直接返回
        }
        if (data.get(search_key) instanceof ListTag)//判断是否为数组
        {
//            LOGGER.info(Msg.ANSI_RED + "搜索数组: " + search_key + Msg.ANSI_RESET);//TODO test
            for (Tag tag : (ListTag) data.get(search_key))//遍历数组里面的所有的内容
            {
                //递归搜索数组里面的每一个元素
                CompoundTag data_in_list = (CompoundTag) tag;
                if (containKey(search_index + 1, data_in_list, nodes_list)) {
                    return true;//直接返回 true
                }
            }
            return false;//如果数组里面没有包含的话就返回 false
        } else if (data.contains(search_key))//判断是否包含当前的标签
        {
            search_index += 1;//索引加一
//            LOGGER.info(Msg.ANSI_RED + "包含体:目前检测键: " + search_key + "索引: " + Integer.toString(search_index) + Msg.ANSI_RESET);//TODO test
            data = data.getCompound(search_key);//获取下一个元素的数据
            return containKey(search_index, data, nodes_list);//继续递归进行判断
        } else {
//            LOGGER.info(Msg.ANSI_RED + "不含有键: " + search_key + Msg.ANSI_RESET);//TODO test
            return false;
        }
    }

    //searching_index: 正在搜索的节点
    //target_val: 目标的值
    //is_judge_value_mode 判断是否还需要去判断这个值是否和和规则一样
    private boolean isValueMatch(int search_index, List<String> target_val, CompoundTag data, List<String> nodes_list)//判断是否包含着这个元素
    {
        String search_key = nodes_list.get(search_index);
//        LOGGER.info(Msg.ANSI_BLUE + "正在检测键: " + search_key + "索引: " + Integer.toString(search_index) + Msg.ANSI_RESET);//TODO test

        if (data.get(search_key) instanceof ListTag)//判断是否为数组
        {
//            LOGGER.info(Msg.ANSI_BLUE + "搜索数组: " + search_key + Msg.ANSI_RESET);//TODO test
            for (Tag tag : (ListTag) data.get(search_key))//遍历数组里面的所有的内容
            {
                //递归搜索数组里面的每一个元素
                CompoundTag data_in_list = (CompoundTag) tag;
                if (isValueMatch(search_index + 1, target_val, data_in_list, nodes_list)) {
                    return true;//直接返回 true
                }
            }
            return false;//如果数组里面没有包含的话就返回 false
        } else if ((nodes_list.size() - 1) <= (search_index)) {//判断是否已经到了最后一个元素了
            String value = data.getString(nodes_list.get(search_index));//获取值的内容
//            LOGGER.info(Msg.ANSI_BLUE + "目标键的路径1的值为:" + value + Msg.ANSI_RESET);//TODO test
            if (FilterUtil.IsStringListContainTarget(target_val, value)) {
//                LOGGER.info(Msg.ANSI_BLUE + "第一个键的值与期望值相同" + Msg.ANSI_RESET);//TODO test
            }
            return FilterUtil.IsStringListContainTarget(target_val, value);//判断目标的标签的值是不是和目标值相同
        } else if (data.contains(search_key))//判断是否包含当前的标签
        {
            search_index += 1;//索引加一
//            LOGGER.info(Msg.ANSI_BLUE + "包含体:目前检测键: " + search_key + "索引: " + Integer.toString(search_index) + Msg.ANSI_RESET);//TODO test
            data = data.getCompound(search_key);//获取下一个元素的数据
            return isValueMatch(search_index, target_val, data, nodes_list);//继续递归进行判断
        } else {
//            LOGGER.info(Msg.ANSI_BLUE + "不含有键: " + search_key + Msg.ANSI_RESET);//TODO test
            return false;
        }
    }

    private boolean isJudgeTypeMatch(CompoundTag nbt_data)//判断这个规则是否匹配判断规则
    {
        boolean is_contain = isValueMatch(0, value1, nbt_data, key1);//是否包含了这个键和值
        boolean res = false;//比较的结果

        if (is_contain && judgeType == JudgeType.IF_CONTAIN)//如果包含
        {
            res = true;//蓝图和规则匹配
        } else if (!is_contain && judgeType == JudgeType.IF_CONTAIN) {
            res = false;
        } else if (!is_contain && judgeType == JudgeType.IF_NOT_CONTAIN)//如果没有包含,并且规则是不包含
        {
            res = true;
        } else if (is_contain && judgeType == JudgeType.IF_NOT_CONTAIN)//如果没有包含,并且规则是不包含
        {
            res = false;
        }
        return res;//返回判断的结果
    }

    private boolean isRuleTypeMatch(CompoundTag nbt_data) throws Exception//判断规则是否匹配
    {
        switch (type) {//判断类型

            case EQUAL: {//如果值和目标的值相同的话,就证明含有非法内容, 如果不相同的话,就证明不含有非法内容
                return isValueMatch(0, value2, nbt_data, key2);
            }

            case NOT_EQUAL: {//如果值和目标的值不相同的话,就有非法内容,如果值和目标相同的话就不含有非法内容
                return !(isValueMatch(0, value2, nbt_data, key2));
            }

            case CONTAIN: {//如果包含的话,就是证明有非法内容
                return containKey(0, nbt_data, key2);
            }
            case NOT_CONTAIN: {//如果没有包含的话,就证明不含有非法内容
                return !(containKey(0, nbt_data, key2));
            }
            default: {
                throw new Exception("Invalid enum of rule type.");
            }
        }
    }

    public boolean IsBlueprintMatch(CompoundTag nbt_data)//判断蓝图数据是否匹配规则
    {
        try {
            if (isJudgeTypeMatch(nbt_data)) {//TODO test
//                LOGGER.info(Msg.ANSI_GREEN + "蓝图判断匹配" + Msg.ANSI_RESET);
            }

            if (isRuleTypeMatch(nbt_data)) {//TODO test
//                LOGGER.info(Msg.ANSI_GREEN + "蓝图过滤规则匹配" + Msg.ANSI_RESET);
            }
            return (isJudgeTypeMatch(nbt_data) && isRuleTypeMatch(nbt_data));//判断是否满足第一个判断条件和第二个判断条件
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(Msg.ANSI_RED + "无法校验蓝图" + Msg.ANSI_RESET);
            return true;//无法校验的时候禁止上传蓝图
        }
    }

    public Rule(String _name, RuleType _type, JudgeType _judgeType, String _key1, String _key2, List<String> _value1, List<String> _value2)//构造函数
    {
        type = _type;//设置过滤的类型
        judgeType = _judgeType;//设置判断的类型

        String[] key1_parts = _key1.split("\\.");//分割字符串
        key1 = Arrays.asList(key1_parts);

        String[] key2_parts = _key2.split("\\.");//分割字符串
        key2 = Arrays.asList(key2_parts);

        name = _name;
        value1 = _value1;
        value2 = _value2;
    }
}
