## 规则配置

- 如果一个路径存在于数组后面,直接写数组的名字即可
- 如果一个NBT数据匹配了第一个判断规则(key1 value1)并且和第二个规则(key2 value2)匹配的话就为非法的蓝图


规则的判断类型
~~~
    MUST_CONTAIN,//必须包含 NBT 标签 (对应的路径下面必须要包含 Rule Value2 里面的 NBT 标签)
    CAN_NOT_INCLUDE,//禁止包含的 BNT 标签 (对应的路径下面禁止包含的 Rule Value2 里面的 NBT 标签)
    MUST_EQUAL, //NBT 标签下的路径必须和 Rule Value 相等
    CAN_NOT_EQUAL //NBT 标签下的路径必须和 Rule Value 不相等
~~~

规则的判断模式

~~~ 
    IF_CONTAIN,//如果包含了值1的路径
    IF_NOT_CONTAIN//如果没有包含值1的路径
~~~

