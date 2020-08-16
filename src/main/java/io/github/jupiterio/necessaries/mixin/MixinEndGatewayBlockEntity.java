package io.github.jupiterio.necessaries.mixin;

import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockView;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Overwrite;

import org.apache.logging.log4j.Logger;

@Mixin(EndGatewayBlockEntity.class)
public class MixinEndGatewayBlockEntity extends EndPortalBlockEntity {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private BlockPos exitPortalPos;

    private RegistryKey<World> exitDimension;

    /* fromTag/toTag
    * We're just saving the exit dimension to the portal's block entity's nbt
    */
    @Inject(method = "toTag", at = @At("RETURN"))
    private void onToTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.exitDimension != null) {
            tag.putString("ExitDimension", this.exitDimension.getValue().toString());
        }
    }

    @Inject(method = "fromTag", at = @At("RETURN"))
    private void onFromTag(BlockState state, CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ExitDimension", 8)) {
            this.exitDimension = RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("ExitDimension")));
        }
    }

    @Inject(method = "tryTeleportingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;teleport(DDD)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    /*
    * Vanilla minecraft's behavior is to teleport entities with their vehicle and passengers. Since all
    * dimension-switching teleporting functions detach vehicles and passengers, and I thought that keeping
    * them wasn't all that important (at least in the server where I'll be using this mod), I decided to
    * change out the teleporting behavior with one much more similar to the /teleport command
    */
    private void onTeleporting(Entity entity, CallbackInfo ci, BlockPos blockpos, Entity pearlOwner) {
        if (entity instanceof EnderPearlEntity) {
            entity = pearlOwner;
        }

        if (this.exitDimension == null) {
            teleport(entity, (ServerWorld)entity.world, blockpos);
        } else {
            World world = entity.getServer().getWorld(this.exitDimension);
            teleport(entity, (ServerWorld)world, blockpos);
        }
    }

    @Redirect(method = "tryTeleportingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;teleport(DDD)V", ordinal = 0))
    // Could have put the code above here, but I believe I'll have a lot of arguments
    // and I'm not completely comfortable with mixins yet
    private void cancelOldTeleport(Entity entity, double x, double y, double z) {
        return; // cancel
    }

//     @ModifyArg(method = "findBestPortalExitPos", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/EndGatewayBlockEntity;findExitPortalPos(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;", ordinal = 0), index = 0)
//     private BlockView changeDestination(BlockView world, BlockPos pos, int i, boolean z) {
//         if (this.exitDimension == null) {
//             return world;
//         } else {
//             World destination = ((ServerWorld)world).getServer().getWorld(this.exitDimension);
//             return destination;
//         }
//     }

    @Overwrite
    private BlockPos findBestPortalExitPos() {
        BlockPos blockPos;
        if (this.exitDimension == null) {
            blockPos = findExitPortalPos(this.world, this.exitPortalPos, 5, false);
        } else {
            World destination = ((ServerWorld)this.world).getServer().getWorld(this.exitDimension);
            blockPos = findExitPortalPos(destination, this.exitPortalPos, 5, false);
        }
        LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortalPos, blockPos);
        return blockPos.up();
    }

    @Shadow
    private static BlockPos findExitPortalPos(BlockView world, BlockPos pos, int searchRadius, boolean bl) {
        return new BlockPos(0,0,0);
    }

    // Teleporting behavior based on net.minecraft.server.command.TeleportCommand$teleport
    private static void teleport(Entity target, ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        if (target instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) target;

            player.teleport(world, x, y, z, player.yaw, player.pitch);
         } else {
            if (target instanceof PathAwareEntity) {
                ((PathAwareEntity)target).getNavigation().stop();
            }

            if (world == target.world) {
                target.refreshPositionAndAngles(x, y, z, target.yaw, target.pitch);
            } else {
                target.detach();
                Entity newTarget = target.getType().create(world);
                if (newTarget == null) {
                    return;
                }

                newTarget.copyFrom(target);
                newTarget.refreshPositionAndAngles(x, y, z, newTarget.yaw, newTarget.pitch);
                world.onDimensionChanged(newTarget);
                target.removed = true;
            }
         }
    }

}
