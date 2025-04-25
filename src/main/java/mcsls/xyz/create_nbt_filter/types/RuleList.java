package mcsls.xyz.create_nbt_filter.types;

import mcsls.xyz.create_nbt_filter.model.OriginalRule;

import java.util.ArrayList;
import java.util.List;

public class RuleList {//解析 yaml 规则的信息储存类
    private List<Rule> ruleList;//规则的列表
    private String BLOCK_PATH = "blocks.nbt.id";//方块位于 NBT 数据的路径

    public String version;//规则的版本

    public RuleList(String version) {//规则列表
        this.version = version;
    }


}
