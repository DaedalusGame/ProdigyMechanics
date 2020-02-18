package prodigymechanics;

import lykrast.prodigytech.common.capability.HotAirChangeable;
import lykrast.prodigytech.common.util.TemperatureHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HotAirTop extends HotAirChangeable {
    public void updateInTemperature(World world, BlockPos pos) {
        this.temperature = TemperatureHelper.getBlockTemp(world, pos.down());
    }

    public int getInAirTemperature() {
        return this.temperature;
    }

    @Override
    public int getOutAirTemperature() {
        return 0;
    }
}
