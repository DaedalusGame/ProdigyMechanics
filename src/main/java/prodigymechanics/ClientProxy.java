package prodigymechanics;

import net.minecraft.client.Minecraft;

public class ClientProxy implements IProxy {
    @Override
    public void makeSmokeParticle(double x, double y, double z, float motionx, float motiony, float motionz, int temperature, float time) {
        ParticleHeatSmoke smoke = new ParticleHeatSmoke(Minecraft.getMinecraft().world, x,y,z,motionx,motiony,motionz,temperature,time);
        Minecraft.getMinecraft().effectRenderer.addEffect(smoke);
    }
}
