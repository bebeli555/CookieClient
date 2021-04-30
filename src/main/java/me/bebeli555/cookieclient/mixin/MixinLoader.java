package me.bebeli555.cookieclient.mixin;

import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class MixinLoader implements IFMLLoadingPlugin {
    public MixinLoader () {
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.cookieclient.json", "mixins.baritone.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String[] getASMTransformerClass () {
        return new String[0];
    }

    @Override
    public String getModContainerClass () {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass () {
        return null;
    }

    @Override
    public void injectData (Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass () {
        return null;
    }
}
