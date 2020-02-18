package prodigymechanics;

import lykrast.prodigytech.common.capability.CapabilityHotAir;
import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileHotAirEngine extends TileEntity implements ITickable {
    protected HotAirTop hotAir;
    public DefaultMechCapability mechPower = new DefaultMechCapability() {
        @Override
        public void setPower(double value, EnumFacing from) {
            if (from == null)
                super.setPower(value, null);
        }

        @Override
        public void onPowerChange() {
            updateNeighbors();
            markDirty();
        }
    };

    public TileHotAirEngine() {
        this.hotAir = new HotAirTop();
    }

    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            this.hotAir.updateInTemperature(this.world, this.pos);
            mechPower.setPower(MathHelper.clamp((this.hotAir.getInAirTemperature() - 30) / 8.0,0,200), null);
            updateNeighbors();
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        mechPower.setPower(0f, null);
    }

    public void updateNeighbors() {
        EnumFacing facing = getFacing();
        TileEntity tile = world.getTileEntity(getPos().offset(facing));
        if (tile != null) {
            if (tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite())) {
                if (tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).isInput(facing.getOpposite())) {
                    tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).setPower(mechPower.getPower(facing), facing.getOpposite());
                }
            }
        }
    }

    private EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockHotAirEngine.FACING);
    }

    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing())
            return true;
        if (capability == CapabilityHotAir.HOT_AIR && (facing == EnumFacing.UP || facing == null))
            return true;
        return super.hasCapability(capability, facing);
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing())
            return (T) mechPower;
        if (capability == CapabilityHotAir.HOT_AIR && (facing == EnumFacing.UP || facing == null))
            return (T) hotAir;
        return super.getCapability(capability, facing);
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.mechPower.power = compound.getDouble("MechPower");
        this.hotAir.deserializeNBT(compound.getCompoundTag("HotAir"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("MechPower", this.mechPower.power);
        compound.setTag("HotAir", this.hotAir.serializeNBT());
        return compound;
    }
}
