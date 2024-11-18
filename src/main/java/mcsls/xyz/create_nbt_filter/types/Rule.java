package mcsls.xyz.create_nbt_filter.types;

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
    private String name;//规则的名字
    private RuleType type;//规则的类型
    private JudgeType judgeType;//判断的类型

    private String key1;//规则的键1 无论是什么类型,统一转换为 String 进行比较
    private String key2;//规则的键2 无论是什么类型,统一转换为 String 进行比较

    private String value2;//规则的值2 无论是什么类型,统一转换为 String 进行比较
    private String value1;//规则的值1 无论是什么类型,统一转换为 String 进行比较

    public Rule(String _name, RuleType _type,JudgeType _judgeType,String _key1,String _key2 ,String _value1, String _value2)//构造函数
    {
        type = _type;//设置过滤的类型
        judgeType = _judgeType;//设置判断的类型

        key1 = _key1;
        key2 = _key2;
        name = _name;
        value1 = _value1;
        value2 = _value2;
    }
}
