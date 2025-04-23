package mcsls.xyz.create_nbt_filter;

import com.mojang.logging.LogUtils;
import mcsls.xyz.create_nbt_filter.commands.ScanAllUpload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod(Create_nbt_filter.MODID)
public class Create_nbt_filter {
    public static final String MODID = "create_nbt_filter";
    private static final Logger LOGGER = LogUtils.getLogger();
    private Filter filter;//NBT 标签过滤器

    public Create_nbt_filter() {

        filter = new Filter();//创建一个新的过滤器

        try {
            filter.LoadAllRulesFromFile();//加载规则文件
        } catch (Exception e) {
            LOGGER.info(Msg.ANSI_RED + "加载规则文件失败,没有任何规则被加载" + Msg.ANSI_RESET);
            e.printStackTrace();
        }

        MinecraftForge.EVENT_BUS.register(new CheckBlueprint(filter));//注册蓝图上传事件的监听器
        MinecraftForge.EVENT_BUS.addListener(this::onRegCommand);//注册指令注册的事件

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 添加监听器
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public void onRegCommand(RegisterCommandsEvent event) {
        ScanAllUpload scanAllUploadCmd = new ScanAllUpload();
        scanAllUploadCmd.register(filter, event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {//服务器启动事件
        LOGGER.info(Msg.ANSI_GREEN + "机械动力蓝图过滤附属加载完毕" + Msg.ANSI_RESET);
    }
}
