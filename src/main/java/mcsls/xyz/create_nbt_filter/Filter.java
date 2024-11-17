package mcsls.xyz.create_nbt_filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Filter {
    private HashMap<String, List<Rule>> pathRuleMap;//根的键值和规则的映射

    public synchronized void InsertRule(String key, Rule rule)//插入规则
    {
        if (pathRuleMap.containsKey(key)) {
            List<Rule> rules = pathRuleMap.get(key);
            rules.add(rule);//添加进规则数组中
        } else {
            List<Rule> rules = new ArrayList<>();//创建一个新的数组
            rules.add(rule);
            pathRuleMap.put(key, rules);//将规则数组放入哈希表中
        }
    }

    public void LoadAllRulesFromFiles()//从文件中加载所有的规则
    {

    }

    public Filter()//构造函数
    {
        pathRuleMap = new HashMap<>();
    }
}
