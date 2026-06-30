package com.hepdd.gtmthings.api.misc;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.datasynclib.GTDataFixer;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import com.gto.datasynclib.datasream.data.Data;
import com.gto.datasynclib.datasream.data.ListData;
import com.gto.datasynclib.datasream.data.NullData;
import com.gto.datasynclib.util.DataCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class UnlimitedItemStackTransfer extends CustomItemStackHandler {

    public UnlimitedItemStackTransfer(int size) {
        super(size);
    }

    public UnlimitedItemStackTransfer(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public UnlimitedItemStackTransfer(ItemStack stack) {
        super(stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public Data writeData() {
        ListData list = new ListData();
        if (this.isInputLimited) list.addNull();
        ItemStack[] stacks = this.stacks;
        for (int i = 0; i < this.size; ++i) {
            ItemStack stack = stacks[i];
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                var count = stack.getCount();
                itemTag.putInt("Slot", i);
                itemTag.putInt("realCount", count);
                stack.setCount(1);
                stack.save(itemTag);
                stack.setCount(count);
                list.add(DataCodecs.COMPOUND_TAG_CODEC.encode(itemTag));
            }
        }
        return list.isEmpty() ? NullData.INSTANCE : list;
    }

    @Override
    public void readData(@NotNull Data data, int dataVersion) {
        if (dataVersion < 1) {
            GTDataFixer.decodeCustomItemStackHandler(this, data, dataVersion);
        } else {
            ItemStack[] stacks = this.stacks;
            Arrays.fill(stacks, ItemStack.EMPTY);
            if (data == NullData.INSTANCE) return;
            List<Data> list = data.getList();
            int size = list.size();
            int i = 0;
            if (list.getFirst() == NullData.INSTANCE) {
                isInputLimited = true;
                ++i;
            }
            for (; i < size; ++i) {
                var item = DataCodecs.COMPOUND_TAG_CODEC.decode(list.get(i));
                int slot = item.getInt("Slot");
                if (slot >= 0 && slot < size) {
                    var stack = ItemStack.of(item);
                    stack.setCount(item.getInt("realCount"));
                    stacks[slot] = stack;
                }
            }
        }
    }
}
