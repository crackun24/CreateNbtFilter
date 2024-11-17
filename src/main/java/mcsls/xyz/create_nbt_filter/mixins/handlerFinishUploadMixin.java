package mcsls.xyz.create_nbt_filter.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSchematicLoader.class)
public class handlerFinishUploadMixin {
    @Inject(method = "handleFinishedUpload",at=@At("HEAD"),cancellable = true,remap = false)
    public void OnSchematicOnLoaded(ServerPlayer player, String schematic, CallbackInfo ci)
    {
        player.sendSystemMessage(Component.literal("正在检查蓝图文件是否合法..."));
    }
}
