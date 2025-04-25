package mcsls.xyz.create_nbt_filter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OriginalRuleList {
    private String version;

    @JsonProperty("check_belt")
    private boolean checkBelt;

    @JsonProperty("check_chain_conveyor")
    private boolean checkChainConveyor;

    @JsonProperty("conveyor_max_connection")
    private int conveyorMaxConnection;

    private List<OriginalRule> rules;   // 列表对应 YAML 里的 - item
}
