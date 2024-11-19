package mcsls.xyz.create_nbt_filter;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import mcsls.xyz.create_nbt_filter.events.BluePrintUploadEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

public class CheckBlueprint {
    Filter filter;//NBT 过滤器
    private static final Logger LOGGER = LogUtils.getLogger();//获取日志记录器

    public CheckBlueprint(Filter _filter) {
        filter = _filter;
        MinecraftForge.EVENT_BUS.register(this);//注册一个监听器
    }


    @SubscribeEvent
    public void OnBlueprintUpload(BluePrintUploadEvent event)//蓝图上传成功的事件
    {
        try {
            SchematicTableBlockEntity table = event.Table;//获取蓝图桌的实例
            Level world = event.World;//获取蓝图桌的世界
            ServerPlayer player = event.Player;//获取玩家的实例
            String PlayerBlueprintId = event.PlayerBlueprintId;//获取蓝图的 Id

            player.sendSystemMessage(Component.literal("§a正在检查蓝图文件是否合法,检测完成后请重新打开蓝图桌领取蓝图."));

            if (table.isRemoved())//判断蓝图桌是否已经被移除了
            {
                player.sendSystemMessage(Component.literal("§c蓝图桌丢失,请确保蓝图校验的时候不破坏或移动蓝图桌"));
                return;
            }

            if (filter.VerifyBlueprint(event.PlayerBlueprintId)) {//校验蓝图
                table.inventory.setStackInSlot(1, SchematicItem.create(world.holderLookup(Registries.BLOCK), event.BluePrintId, player.getGameProfile()
                        .getName()));//生成对应的蓝图
                player.sendSystemMessage(Component.literal("§a蓝图校验成功"));

            } else {
                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());//生成空白的蓝图
                player.sendSystemMessage(Component.literal("§c蓝图含有非法内容"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
