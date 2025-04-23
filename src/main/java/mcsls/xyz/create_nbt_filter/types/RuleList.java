package mcsls.xyz.create_nbt_filter.types;
import java.util.List;

public class RuleList {//解析 yaml 规则的信息储存类
    public double version;//规则的版本
    public String name;//规则的名字
    public String judge;//判断的方式
    public String type;//使用的规则
    public String key1;//第一个键的列表
    public String key2;//第二个键的列表
    public List<String> value1;//第一个值
    public List<String> value2;//第二个值

    public Rule ToRule() {
        RuleType type = RuleType.valueOf(this.type);//获取规则的类型
        JudgeType judge_type = JudgeType.valueOf(this.judge);//获取判断的方式
        return new Rule(name, type, judge_type, key1, key2, value1, value2);
    }
}
