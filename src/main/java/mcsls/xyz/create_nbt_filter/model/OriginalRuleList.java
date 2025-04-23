package mcsls.xyz.create_nbt_filter.model;

import lombok.Data;

import java.util.List;

@Data
public class OriginalRuleList {
    private String version;
    private List<OriginalRule> rules;

    public OriginalRuleList() {
    }
}
