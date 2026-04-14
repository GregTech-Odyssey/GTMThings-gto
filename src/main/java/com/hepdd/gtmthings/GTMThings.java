package com.hepdd.gtmthings;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.hepdd.gtmthings.data.*;

import static com.hepdd.gtmthings.common.registry.GTMTRegistration.GTMTHINGS_REGISTRATE;
import static net.minecraft.resources.ResourceLocation.tryBuild;

@Mod(GTMThings.MOD_ID)
public class GTMThings {

    public static final String MOD_ID = "gtmthings";
    public static final String NAME = "GTM Things";

    public static ResourceLocation id(String name) {
        return tryBuild(MOD_ID, name);
    }

    public GTMThings(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        GTMTHINGS_REGISTRATE.registerEventListeners(modEventBus);
    }
}
