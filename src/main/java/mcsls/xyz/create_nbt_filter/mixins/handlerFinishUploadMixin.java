package mcsls.xyz.create_nbt_filter.mixins;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import mcsls.xyz.create_nbt_filter.NBTChecker;
import mcsls.xyz.create_nbt_filter.events.BluePrintUploadEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerSchematicLoader.class)
public abstract class handlerFinishUploadMixin {
    @Shadow
    private Map<String, ServerSchematicLoader.SchematicUploadEntry> activeUploads;

    @Shadow
    public abstract SchematicTableBlockEntity getTable(Level world, BlockPos pos);

    @Inject(method = "handleFinishedUpload", at = @At("HEAD"), cancellable = true, remap = false)
    public void OnSchematicOnLoaded(ServerPlayer player, String schematic, CallbackInfo ci) {
        player.sendSystemMessage(Component.literal("§a正在检查蓝图文件是否合法..."));

        String playerSchematicId = player.getGameProfile()
                .getName() + "/" + schematic;

        if (activeUploads.containsKey(playerSchematicId)) {
            try {
                activeUploads.get(playerSchematicId).stream.close();//关闭输入流
                ServerSchematicLoader.SchematicUploadEntry removed = activeUploads.remove(playerSchematicId);
                Level world = removed.world;
                BlockPos pos = removed.tablePos;//获取蓝图桌的位置

                Create.LOGGER.info("New Schematic Uploaded: " + playerSchematicId);
                if (pos == null)
                    return;

                BlockState blockState = world.getBlockState(pos);
                if (AllBlocks.SCHEMATIC_TABLE.get() != blockState.getBlock())
                    return;

                SchematicTableBlockEntity table = getTable(world, pos);//获取蓝图桌实例
                if (table == null)
                    return;
                table.finishUpload();

                BluePrintUploadEvent uploadEvent = new BluePrintUploadEvent(player, "test");
                MinecraftForge.EVENT_BUS.post(uploadEvent);

                if (NBTChecker.CheckBluePrint(playerSchematicId)) {
                    table.inventory.setStackInSlot(1, SchematicItem.create(world.holderLookup(Registries.BLOCK), schematic, player.getGameProfile()
                            .getName()));//生成对应的蓝图
                    player.sendSystemMessage(Component.literal("§a蓝图校验成功"));

                } else {
                    table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());//生成空白的蓝图
                    player.sendSystemMessage(Component.literal("§c蓝图含有非法内容"));
                }

                ci.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}