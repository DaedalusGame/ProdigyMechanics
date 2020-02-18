package prodigymechanics;

import lykrast.prodigytech.common.item.ItemBlockMachineHotAir;
import lykrast.prodigytech.common.util.CreativeTabsProdigyTech;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

@Mod(modid = ProdigyMechanics.MODID, acceptedMinecraftVersions = "[1.12, 1.13)", dependencies = "required-after:prodigytech;required-after:mysticalmechanics")
@Mod.EventBusSubscriber
public class ProdigyMechanics
{
    public static final String MODID = "prodigymechanics";

    @GameRegistry.ObjectHolder("prodigymechanics:hot_air_engine")
    public static BlockHotAirEngine HOT_AIR_ENGINE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        HOT_AIR_ENGINE = (BlockHotAirEngine) new BlockHotAirEngine(6.0F, 45.0F, 1).setRegistryName(MODID, "hot_air_engine").setUnlocalizedName("hot_air_engine").setCreativeTab(CreativeTabsProdigyTech.INSTANCE);

        event.getRegistry().register(HOT_AIR_ENGINE);

        GameRegistry.registerTileEntity(TileHotAirEngine.class,new ResourceLocation(MODID,"hot_air_engine"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockMachineHotAir(HOT_AIR_ENGINE, 30, 0).setRegistryName(HOT_AIR_ENGINE.getRegistryName()));
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(ProdigyMechanics.MODID,"hot_air_engine"), new ItemStack(ProdigyMechanics.HOT_AIR_ENGINE,1),true,new Object[]{
                "III", "AGC", "C C",
                'C', "ingotFerramic",
                'G', "gearFerramic",
                'I', "ingotIron",
                'A', RegistryHandler.IRON_AXLE}).setRegistryName(new ResourceLocation(ProdigyMechanics.MODID,"hot_air_engine")));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(HOT_AIR_ENGINE), 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }
}
