package prodigymechanics;

import lykrast.prodigytech.common.capability.CapabilityHotAir;
import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Random;

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
    Random random = new Random();

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
            mechPower.setPower(MathHelper.clamp((this.hotAir.getInAirTemperature() - ProdigyMechanics.MIN_TEMPERATURE) * ProdigyMechanics.CONVERSION_RATE,0,ProdigyMechanics.MAX_POWER), null);
            updateNeighbors();
            markDirty();
        } else {
            spawnParticles();
        }
    }

    private void spawnParticles() {
        int temperature = hotAir.getInAirTemperature();
        if (temperature <= ProdigyMechanics.MIN_TEMPERATURE)
            return;
        for (int i = 0; i < 4; i++) {
            float offX = 0.09375f + 0.8125f * (float) random.nextInt(2);
            float offZ = 0.28125f + 0.4375f * (float) random.nextInt(2);
            if (getFacing().getAxis() == EnumFacing.Axis.X) {
                float h = offX;
                offX = offZ;
                offZ = h;
            }

            ProdigyMechanics.proxy.makeSmokeParticle(getPos().getX() + offX, getPos().getY() + 1.0, getPos().getZ() + offZ, 0.025f * (random.nextFloat() - 0.5f), 0.125f * 0.5f * (random.nextFloat()), 0.025f * (random.nextFloat() - 0.5f), temperature, 1);
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

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this, false);
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
