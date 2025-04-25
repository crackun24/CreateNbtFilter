package mcsls.xyz.create_nbt_filter.types;

public enum RuleType {
    CONTAIN,//如果包含了指定的键,这个蓝图则为非法的蓝图
    NOT_CONTAIN,//如果没有包含指定的键,这个蓝图就是非法的蓝图
    EQUAL, //NBT 标签下的路径必须和 Rule Value 相等
    NOT_EQUAL, //NBT 标签下的路径必须和 Rule Value 不相等
    RANGE//范围匹配
}
