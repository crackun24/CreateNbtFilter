package mcsls.xyz.create_nbt_filter;

import net.minecraftforge.accesstransformer.generated.AtParser;

enum RuleType {
    MUST_CONTAIN,//必须包含 NBT 标签 (对应的路径下面必须要包含 Rule Value 里面的 NBT 标签)
    CAN_NOT_INCLUDE,//禁止包含的 BNT 标签 (对应的路径下面禁止包含的 Rule Value 里面的 NBT 标签)
    MUST_EQUAL, //NBT 标签下的路径必须和 Rule Value 相等
    CAN_NOT_EQUAL //NBT 标签下的路径必须和 Rule Value 不相等
}

public class Rule {//规则
    private String name;//规则的名字
    private RuleType type;//规则的类型
    private String value;//规则的值 无论是什么类型,统一转换为 String 进行比较

    public Rule(String _name, RuleType _type, String _value)//构造函数
    {
        name = _name;
        type = _type;
        value = _value;
    }
}
