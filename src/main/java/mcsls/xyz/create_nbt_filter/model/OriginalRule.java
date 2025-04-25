package mcsls.xyz.create_nbt_filter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class OriginalRule {
    private String name;
    private String block;

    // YAML 中大小写不规则的键，用 @JsonProperty 精确映射
    @JsonProperty("Univariate")
    public Map<String, Object> univariate;

    @JsonProperty("Redundant")
    public Map<String, Object> redundant;
}
