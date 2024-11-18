package mcsls.xyz.create_nbt_filter.mixins;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import mcsls.xyz.create_nbt_filter.events.BluePrintUploadEvent;
import net.minecraft.core.BlockPos;
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

                BluePrintUploadEvent uploadEvent = new BluePrintUploadEvent(player, playerSchematicId, world, table);//构建一个事件
                MinecraftForge.EVENT_BUS.post(uploadEvent);//触发蓝图上传完成的事件

                ci.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}