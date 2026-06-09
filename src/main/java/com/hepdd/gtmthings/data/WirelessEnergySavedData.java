package com.hepdd.gtmthings.data;

import net.minecraft.nbt.CompoundTag;

/**
 * Correctly spelled facade for wireless energy saved data.
 */
@SuppressWarnings("deprecation")
public class WirelessEnergySavedData extends WirelessEnergySavaedData {

    public WirelessEnergySavedData() {}

    public WirelessEnergySavedData(CompoundTag tag) {
        super(tag);
    }
}
