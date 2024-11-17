package mcsls.xyz.create_nbt_filter;

import mcsls.xyz.create_nbt_filter.events.BluePrintUploadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CheckBlueprint {
    public CheckBlueprint()
    {
        MinecraftForge.EVENT_BUS.register(this);//注册事件
    }

    @SubscribeEvent
    public void OnBlueprintUpload(BluePrintUploadEvent event)//蓝图上传成功的事件
    {

    }
}
