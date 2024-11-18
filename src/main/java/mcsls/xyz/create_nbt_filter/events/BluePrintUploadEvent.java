package mcsls.xyz.create_nbt_filter.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class BluePrintUploadEvent extends Event {//蓝图上传事件
    private final String blueprintId;//蓝图的ID
    private ServerPlayer player;//上传蓝图的玩家

    public BluePrintUploadEvent(ServerPlayer _player, String id) {//构造函数
        blueprintId = id;
        player = _player;
    }
}
