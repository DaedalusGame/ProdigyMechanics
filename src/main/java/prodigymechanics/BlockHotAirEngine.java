package prodigymechanics;

import lykrast.prodigytech.common.block.BlockMachineActiveable;
import lykrast.prodigytech.common.capability.CapabilityHotAir;
import lykrast.prodigytech.common.util.TemperatureHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHotAirEngine extends BlockMachineActiveable<TileHotAirEngine> {
    public BlockHotAirEngine(float hardness, float resistance, int harvestLevel) {
        super(Material.IRON, SoundType.METAL, hardness, resistance, "pickaxe", harvestLevel, TileHotAirEngine.class);
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileHotAirEngine) {
            TemperatureHelper.hotAirDamage(entityIn, ((TileHotAirEngine) tile).hotAir.getInAirTemperature());
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileHotAirEngine) {
            ((TileHotAirEngine)tile).updateNeighbors();
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileHotAirEngine p = (TileHotAirEngine)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileHotAirEngine();
    }
}
