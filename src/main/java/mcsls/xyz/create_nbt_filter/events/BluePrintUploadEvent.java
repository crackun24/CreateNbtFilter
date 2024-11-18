package mcsls.xyz.create_nbt_filter.events;

import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class BluePrintUploadEvent extends Event {//蓝图上传事件
    public final String BlueprintId;//蓝图的ID
    public ServerPlayer Player;//上传蓝图的玩家
    public SchematicTableBlockEntity Table;//蓝图桌的引用
    public Level World;


    public BluePrintUploadEvent(ServerPlayer _player, String id, Level world, SchematicTableBlockEntity _table) {//构造函数
        BlueprintId = id;
        Player = _player;
        Table = _table;
        World = world;
    }
}