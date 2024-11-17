package mcsls.xyz.create_nbt_filter.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

public class BluePrintUploadEvent extends Event {//蓝图上传事件
    private final String blueprintId;//蓝图的ID
    public static void RegEvent(IEventBus event_bus)//注册事件
    {
    }

    public BluePrintUploadEvent(String id) {//构造函数
        blueprintId = id;
    }
}
