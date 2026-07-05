package com.hepdd.gtmthings.integration.jade.provider;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import com.hepdd.gtmthings.GTMThings;
import com.hepdd.gtmthings.api.misc.WirelessTransferBindIndex;
import com.hepdd.gtmthings.api.misc.WirelessTransferBindIndex.Binding;
import org.apache.commons.lang3.StringUtils;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * When looking at a block that a wireless transfer cover is bound to, lists those covers
 * (transfer type, holding block, position, face) in the Jade tooltip. Backed by {@link WirelessTransferBindIndex}.
 */
public final class WirelessTransferBindProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    private static final ResourceLocation UID = GTMThings.id("wireless_transfer_bind_provider");
    private static final String KEY = "binds";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getLevel() instanceof ServerLevel level)) return;
        var bindings = WirelessTransferBindIndex.get(level.dimension(), accessor.getPosition());
        if (bindings.isEmpty()) return;
        ListTag list = new ListTag();
        for (Binding b : bindings) {
            CompoundTag tag = new CompoundTag();
            tag.putString("d", b.coverDim().location().toString());
            tag.putInt("x", b.coverPos().getX());
            tag.putInt("y", b.coverPos().getY());
            tag.putInt("z", b.coverPos().getZ());
            tag.putString("f", b.coverFace().getName());
            tag.putString("t", b.transferType() == 2 ? "gtmthings.wireless_transfer.type.fluid" : "gtmthings.wireless_transfer.type.item");
            tag.putString("b", b.coverBlockId());
            list.add(tag);
        }
        data.put(KEY, list);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        ListTag list = accessor.getServerData().getList(KEY, Tag.TAG_COMPOUND);
        if (list.isEmpty()) return;
        String lookedDim = accessor.getLevel().dimension().location().toString();
        tooltip.add(Component.translatable("gtmthings.wireless_transfer.bind_title").withStyle(ChatFormatting.GRAY));
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            String pos = "(" + tag.getInt("x") + ", " + tag.getInt("y") + ", " + tag.getInt("z") + ")";
            tooltip.add(Component.translatable("gtmthings.wireless_transfer.bind_entry",
                    Component.translatable(tag.getString("t")),
                    Component.translatable(tag.getString("b")),
                    pos,
                    StringUtils.capitalize(tag.getString("f"))));
            String coverDim = tag.getString("d");
            if (!coverDim.equals(lookedDim)) {
                tooltip.add(Component.translatable("gtmthings.wireless_transfer.dim_suffix", dimensionDisplay(coverDim)));
            }
        }
    }

    /** Raw id path: strips the {@code minecraft:} namespace, keeps {@code modid:path} for modded dimensions. */
    private static String dimensionDisplay(String dimensionId) {
        return dimensionId.startsWith("minecraft:") ? dimensionId.substring("minecraft:".length()) : dimensionId;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
