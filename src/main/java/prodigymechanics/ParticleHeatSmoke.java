package prodigymechanics;

import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleHeatSmoke extends ParticleSmokeNormal {
    int temperature;
    float black;

    protected ParticleHeatSmoke(World worldIn, double x, double y, double z, double motionx, double motiony, double motionz, int temperature, float time) {
        super(worldIn, x, y, z, motionx, motiony, motionz, time);
        this.temperature = temperature;
        this.black = (float)(Math.random() * 0.3);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.particleAge >= this.particleMaxAge / 2) {
            this.particleRed = black;
            this.particleGreen = black;
            this.particleBlue = black;
        } else {
            float tempVisual = MathHelper.clamp((float)MathHelper.clampedLerp(0,15000, temperature/1000.0),1500f, 15000f);
            this.particleRed = getHeatRed(tempVisual);
            this.particleGreen = getHeatGreen(tempVisual);
            this.particleBlue = getHeatBlue(tempVisual);
        }
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        int i = super.getBrightnessForRender(p_189214_1_);
        int j = 240;
        int k = i >> 16 & 255;
        if(this.particleAge >= this.particleMaxAge / 2)
            return i;
        else
            return j | k << 16;
    }

    private float getHeatRed(float temperature) {
        temperature /= 100;
        if(temperature <= 66)
            return 1.0f;
        else
            return MathHelper.clamp(329.698727446f * (float)Math.pow(temperature - 60,-0.1332047592f),0f, 255f) / 255f;
    }

    private float getHeatGreen(float temperature) {
        temperature /= 100;
        if(temperature <= 66)
            return MathHelper.clamp(99.4708025861f * (float)Math.log(temperature) - 161.1195681661f,0f, 255f) / 255f;
        else
            return MathHelper.clamp(288.1221685293f * (float)Math.pow(temperature - 60,-0.075148492f),0f, 255f) / 255f;
    }

    private float getHeatBlue(float temperature) {
        temperature /= 100;
        if(temperature > 66)
            return 1.0f;
        else if (temperature > 16)
            return MathHelper.clamp(138.5177312231f * (float)Math.log(temperature - 10) - 305.0447927307f,0f, 255f) / 255f;
        else
            return 0.0f;
    }
}
