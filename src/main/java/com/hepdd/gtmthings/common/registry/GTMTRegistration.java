package com.hepdd.gtmthings.common.registry;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.hepdd.gtmthings.GTMThings;
import com.hepdd.gtmthings.common.block.machine.multiblock.part.MEOutputPartMachine;

import java.util.function.Function;

public class GTMTRegistration {

    public static Function<MetaMachineBlockEntity, MetaMachine> ME_OUTPUT = MEOutputPartMachine::new;

    public static GTRegistrate GTMTHINGS_REGISTRATE = GTRegistrate.create(GTMThings.MOD_ID);

    static {
        GTMTRegistration.GTMTHINGS_REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    private GTMTRegistration() {/**/}
}
