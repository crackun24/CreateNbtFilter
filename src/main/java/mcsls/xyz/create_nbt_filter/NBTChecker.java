package mcsls.xyz.create_nbt_filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.FileInputStream;

public class NBTChecker {// NBT 检查类
private static final String SCHEMATICS_ROOT ="./schematics/uploaded/";//蓝图文件的根目录
   private static Boolean verifyNBT(CompoundTag nbt)//校验NBT标签
   {
        return true;
   }

    public static Boolean CheckBluePrint(String playerSchematicId)//根据蓝图的 ID 检查蓝图是否合法
    {
        String filePath = SCHEMATICS_ROOT +playerSchematicId;//拼接出蓝图文件的路径
        try{
            FileInputStream in = new FileInputStream(filePath);//读取 NBT 文件
            CompoundTag nbt_file = NbtIo.readCompressed(in);

            if(nbt_file == null)
            {
                throw new Exception("Could not parse NBT FIle.");
            }
            return verifyNBT(nbt_file) ;//校验 NBT 文件
        }catch (Exception e)
        {
            e.printStackTrace();
           return false;
        }
    }
}
