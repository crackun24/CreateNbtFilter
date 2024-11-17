package mcsls.xyz.create_nbt_filter;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Create_nbt_filter.MODID)
public class Create_nbt_filter {

    public static final String MODID = "create_nbt_filter";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Create_nbt_filter() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 添加监听器
        modEventBus.addListener(this::commonSetup);
        //注册模组
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {//服务器启动事件
        LOGGER.info(Msg.ANSI_GREEN + "The Create NBT filter is loaded" + Msg.ANSI_RESET);
    }
}
