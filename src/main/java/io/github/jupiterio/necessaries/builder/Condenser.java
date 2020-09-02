package io.github.jupiterio.necessaries.builder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Condenser {
    public static void condenseAll(PlayerInventory inventory) {
        for(int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);

            if (stack.isEmpty()) continue;

            int maxCount = stack.getMaxCount();

            if (maxCount > 1 && stack.getCount() == maxCount) {
                ItemStack condensedStack = new ItemStack(Items.STICK);
                CompoundTag tag = condensedStack.getOrCreateTag();

                tag.putInt("CustomModelData", 1);
                tag.put("Compressed", stack.toTag(new CompoundTag()));

                MutableText name = stack.getName().shallowCopy();
                name.setStyle(name.getStyle().withItalic(false));
                condensedStack.setCustomName(name);

                CompoundTag displayOrig = stack.getSubTag("display");
                CompoundTag displayCond = condensedStack.getSubTag("display");
                ListTag newLore;
                if (displayOrig != null) {
                    newLore = displayOrig.getList("Lore", 8).copy();
                } else {
                    newLore = new ListTag();
                }

                newLore.add(0, StringTag.of(Text.Serializer.toJson(new TranslatableText("pcd.condenser.compressed", maxCount))));
                displayCond.put("Lore", newLore);

                inventory.removeStack(i);
                inventory.insertStack(condensedStack);
                // TODO: CHECK IF THERE'S NO EMPTY SLOTS
            }
        }
    }
}
