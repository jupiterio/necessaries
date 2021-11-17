package io.github.jupiterio.necessaries.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.OnAStickItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import io.github.jupiterio.necessaries.claim.ClaimManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onInteractItem(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (world.isClient()) return;

        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() == Items.PAPER) {
            NbtCompound tag = stack.getTag();

            if (tag != null && tag.contains("CustomModelData", 3)) {

                int modelData = tag.getInt("CustomModelData");

                if (modelData == 1) { // UNNAMED CLAIMER
                    int id = ClaimManager.createClaim(player, stack);
                    tag.putInt("CustomModelData", 2);
                    tag.putInt("ClaimId", id);

                    cir.setReturnValue(TypedActionResult.success(stack));
                } else if (modelData == 2) { // KINGDOM CLAIMER
                    if(!ClaimManager.claim(world, player, stack)) {
                        System.out.println("Claim failed for " + player.getName().asString() + " at " + player.getBlockPos().toString());
                    }

                    cir.setReturnValue(TypedActionResult.success(stack));
                } else if (modelData == 3) { // UNCLAIMER
                    if (!ClaimManager.unclaim(world, player)) {
                        System.out.println("Unclaim failed for " + player.getName().asString() + " at " + player.getBlockPos().toString());
                    }

                    cir.setReturnValue(TypedActionResult.success(stack));
                } else {
                    cir.setReturnValue(TypedActionResult.pass(stack));
                }
            }
        }

        if (stack.getItem() == Items.QUARTZ) {
            NbtCompound tag = stack.getTag();

            if (tag != null && tag.contains("CustomModelData", 3)) {

                int modelData = tag.getInt("CustomModelData");

                if (modelData == 1) { // POCKET DIMENSION
                    // implement
                } else {
                    cir.setReturnValue(TypedActionResult.pass(stack));
                }
            }
        }
    }

}
