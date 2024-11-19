package mcsls.xyz.create_nbt_filter.types;

import com.mojang.logging.LogUtils;
import mcsls.xyz.create_nbt_filter.Msg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

enum RuleType {
    MUST_CONTAIN,//必须包含 NBT 标签 (对应的路径下面必须要包含 Rule Value2 里面的 NBT 标签)
    CAN_NOT_INCLUDE,//禁止包含的 BNT 标签 (对应的路径下面禁止包含的 Rule Value2 里面的 NBT 标签)
    MUST_EQUAL, //NBT 标签下的路径必须和 Rule Value 相等
    CAN_NOT_EQUAL //NBT 标签下的路径必须和 Rule Value 不相等
}

enum JudgeType {//判断模式
    IF_CONTAIN,//如果包含了值1的路径
    IF_NOT_CONTAIN//如果没有包含值1的路径
}

public class Rule {//规则
    private static final Logger LOGGER = LogUtils.getLogger();//获取日志记录器
    private String name;//规则的名字
    private RuleType type;//规则的类型
    private JudgeType judgeType;//判断的类型

    private List<String> key1;//规则的键1 无论是什么类型,统一转换为 String 进行比较
    private List<String> key2;//规则的键2 无论是什么类型,统一转换为 String 进行比较

    private String value2;//规则的值2 无论是什么类型,统一转换为 String 进行比较
    private String value1;//规则的值1 无论是什么类型,统一转换为 String 进行比较


    private Boolean containKey1(CompoundTag nbt_data)//判断是否包含键1的值
    {
        CompoundTag temp = nbt_data;
        for (int i = 0; i < key1.size(); i++)//遍历所有的键
        {
            String key = key1.get(i);//获取当前遍历的键
            LOGGER.info(Msg.ANSI_BLUE + key + Msg.ANSI_RESET);//TODO test

            if (!key.contains(key))//判断是否含有当前的键
            {
                return false;
            }

            if (nbt_data.get(key) instanceof ListTag)//判断是否为数组
            {
                String next_key = key1.get(i + 1);//获取下一个元素
                temp = getNBTObjInArray(nbt_data.getList(key, ListTag.TAG_COMPOUND), next_key);//获取数组中包含该键的元素的对象
                if (temp == null)//数组中不存在
                {
                    return false;
                } else {
                    i += 1;//数组中的元素被找到了,跳过下一个键
                }
            } else {
                temp = temp.getCompound(key);//获取下一层级的数据
            }
        }
        return true;
    }

    public Boolean IsBlueprintMatch(CompoundTag nbt_data)//判断蓝图数据是否匹配规则
    {
        if (!containKey1(nbt_data))//判断是否包含键1
        {
            return false;
        }
        return true;
    }

    public Rule(String _name, RuleType _type, JudgeType _judgeType, String _key1, String _key2, String _value1, String _value2)//构造函数
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
