package mcsls.xyz.create_nbt_filter;

import mcsls.xyz.create_nbt_filter.events.BluePrintUploadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CheckBlueprint {
    public CheckBlueprint()
    {
        MinecraftForge.EVENT_BUS.register(this);//注册一个监听器
    }

    @SubscribeEvent
    public void OnBlueprintUpload(BluePrintUploadEvent event)//蓝图上传成功的事件
    {
        System.out.println("test form event handler.");//TODO test
    }
}
