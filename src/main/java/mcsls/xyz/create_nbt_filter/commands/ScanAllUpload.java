package mcsls.xyz.create_nbt_filter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import mcsls.xyz.create_nbt_filter.Filter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


//扫描所有上传的蓝图的指令
public class ScanAllUpload {
    private Filter filter;

    public void register(Filter filter1, CommandDispatcher<CommandSourceStack> dispatcher) {
        filter = filter1;

        dispatcher.register(Commands.literal("fullscan").executes(
                        commandContext -> {
                            if (commandContext.getSource().hasPermission(4)) {//判断是否有权限执行
                                new Thread(() -> {

                                    try {
                                        filter.FullScan();//异步执行扫描
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }).start();
                                commandContext.getSource().sendSystemMessage(Component.literal("请到控制台查看执行结果"));
                            } else {
                                commandContext.getSource().sendSystemMessage(Component.literal("权限不足"));
                            }

                            return Command.SINGLE_SUCCESS;
                        }
                )
        );
    }
}
