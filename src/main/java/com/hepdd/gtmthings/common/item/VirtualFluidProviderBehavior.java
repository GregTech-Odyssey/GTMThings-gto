package com.hepdd.gtmthings.common.item;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.gui.widget.TankWidget;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.transfer.fluid.ICustomFluidStackHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.hepdd.gtmthings.data.CustomItems;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nullable;

import static net.minecraft.resources.ResourceLocation.tryBuild;

public final class VirtualFluidProviderBehavior implements IAddInformation, IItemUIFactory, IFancyUIProvider {

    public static final VirtualFluidProviderBehavior INSTANCE = new VirtualFluidProviderBehavior();

    public static ItemStack setVirtualFluid(ItemStack stack, FluidStack virtualFluid) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.remove("t");
        if (virtualFluid.isEmpty()) {
            tag.remove("m");
            tag.putString("n", "empty");
        } else {
            ResourceLocation id = ForgeRegistries.FLUIDS.getKey(virtualFluid.getFluid());
            tag.putString("m", id.getNamespace());
            tag.putString("n", id.getPath());
            CompoundTag itemTag = virtualFluid.getTag();
            if (itemTag != null) tag.put("t", itemTag);
        }
        return stack;
    }

    public static FluidStack getVirtualFluid(final ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) return FluidStack.EMPTY;
        var mod = tag.getString("m");
        if (mod.isEmpty()) return FluidStack.EMPTY;
        var fluid = ForgeRegistries.FLUIDS.getValue(tryBuild(mod, tag.getString("n")));
        if (fluid == null || fluid == Fluids.EMPTY) return FluidStack.EMPTY;
        var fluidStack = new FluidStack(fluid, 1000);
        if (tag.get("t") instanceof CompoundTag compoundTag) fluidStack.setTag(compoundTag);
        return fluidStack;
    }

    private InteractionHand hand;

    @Override
    public void appendTooltips(@NotNull ItemStack itemstack, @Nullable Level world, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (itemstack.hasTag()) {
            list.add(Component.translatable("gui.ae2.Fluids").append(": "));
            list.add(getVirtualFluid(itemstack).getDisplayName());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        hand = usedHand;
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        return new ModularUI(176, 166, holder, entityPlayer).widget(new FancyMachineUIWidget(this, 176, 166));
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        WidgetGroup group = new WidgetGroup(0, 0, 18 + 16, 18 + 16);
        WidgetGroup container = new WidgetGroup(4, 4, 18 + 8, 18 + 8);
        container.addWidget(new TankWidget(new FluidHandler(widget.getGui().entityPlayer, hand), 0, 4, 4, true, true).setBackground(GuiTextures.SLOT));
        group.addWidget(container);
        return group;
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(CustomItems.VIRTUAL_FLUID_PROVIDER.get());
    }

    @Override
    public Component getTitle() {
        return CustomItems.VIRTUAL_FLUID_PROVIDER.get().getDescription();
    }

    private static class FluidHandler implements ICustomFluidStackHandler {

        private ItemStack getItem() {
            return entityPlayer.getItemInHand(hand);
        }

        private FluidStack virtualFluid;
        private final Player entityPlayer;
        private final InteractionHand hand;

        private FluidHandler(Player entityPlayer, InteractionHand hand) {
            this.entityPlayer = entityPlayer;
            this.hand = hand;
        }

        @Override
        public void setFluidInTank(int i, FluidStack fluidStack) {
            if (entityPlayer.isLocalPlayer()) return;
            virtualFluid = ICustomFluidStackHandler.copy(fluidStack, 1000);
            entityPlayer.setItemInHand(hand, setVirtualFluid(getItem(), virtualFluid));
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int i) {
            if (virtualFluid == null) virtualFluid = getVirtualFluid(getItem());
            return virtualFluid;
        }

        @Override
        public int getTankCapacity(int i) {
            return 1000;
        }

        @Override
        public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
            entityPlayer.isLocalPlayer();
            return true;
        }

        @Override
        public int fill(FluidStack fluidStack, FluidAction fluidAction) {
            if (entityPlayer.isLocalPlayer() || fluidStack.isEmpty() || fluidStack.getAmount() < 1000) return 0;
            virtualFluid = ICustomFluidStackHandler.copy(fluidStack, 1000);
            entityPlayer.setItemInHand(hand, setVirtualFluid(getItem(), virtualFluid));
            return 1000;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
            if (!getFluidInTank(0).isFluidEqual(fluidStack) || fluidStack.getAmount() < 1000) return FluidStack.EMPTY;
            return drain(0, fluidAction);
        }

        @Override
        public @NotNull FluidStack drain(int i, FluidAction fluidAction) {
            if (i != 0 || entityPlayer.isLocalPlayer() || getItem().getOrCreateTag().getBoolean("marked")) return FluidStack.EMPTY;
            var old = getFluidInTank(0);
            entityPlayer.setItemInHand(hand, setVirtualFluid(getItem(), FluidStack.EMPTY));
            virtualFluid = FluidStack.EMPTY;
            return old;
        }
    }
}
