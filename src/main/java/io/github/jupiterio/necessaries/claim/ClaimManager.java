package io.github.jupiterio.necessaries.claim;

import io.github.jupiterio.necessaries.ComponentManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.world.WorldProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class ClaimManager {
    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            Iterator playersIter = players.iterator();

            while(playersIter.hasNext()) {
                ServerPlayerEntity player = (ServerPlayerEntity) playersIter.next();

                ComponentProvider chunkProvider = ComponentProvider.fromChunk(player.world.getChunk(player.getBlockPos()));

                ClaimComponent chunkClaim = (ClaimComponent) ComponentManager.CLAIM.get(chunkProvider);
                ClaimComponent playerClaim = (ClaimComponent) ComponentManager.CLAIM.get(player);

                if (chunkClaim.getId() != playerClaim.getId()) {
                    Claim chunkClaimData = chunkClaim.getClaimData();
                    playerClaim.setId(chunkClaim.getId());
                    player.networkHandler.sendPacket(new SubtitleS2CPacket(chunkClaimData.getName()));
                    player.networkHandler.sendPacket(new TitleS2CPacket(new LiteralText(" ")));
                }
            }
        });
    }

    public static boolean claim(World world, PlayerEntity player, ItemStack stack) {
        ComponentProvider chunkProvider = ComponentProvider.fromChunk(world.getChunk(player.getBlockPos()));

        ClaimComponent chunkClaim = (ClaimComponent) ComponentManager.CLAIM.get(chunkProvider);
        ClaimListComponent claimComponent = (ClaimListComponent) ComponentManager.CLAIM_LIST.get(world.getLevelProperties());
        List<Claim> claims = claimComponent.getClaims();

        if (chunkClaim.getId() == 0) {
            System.out.println("We're in wilderness");
            NbtCompound tag = stack.getTag();
            if (tag != null && tag.contains("ClaimId", 3)) {
                System.out.println("We have an int ClaimId");
                int id = tag.getInt("ClaimId");

                Claim currentClaim = claimComponent.getClaim(id);
                System.out.println(currentClaim.getName().asString());

                if (currentClaim.getOwner().equals(player.getUuid())) {
                    System.out.println("Same owners");
                    chunkClaim.setId(id);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean unclaim(World world, PlayerEntity player) {
        ComponentProvider chunkProvider = ComponentProvider.fromChunk(world.getChunk(player.getBlockPos()));

        ClaimComponent chunkClaim = (ClaimComponent) ComponentManager.CLAIM.get(chunkProvider);

        if (chunkClaim.getId() != 0) {
            System.out.println("We're not in wilderness");
            Claim currentClaim = chunkClaim.getClaimData();
            System.out.println(currentClaim.getName().asString());

            if (currentClaim.getOwner().equals(player.getUuid())) {
                System.out.println("Same owners");
                chunkClaim.setId(0);
                return true;
            }
        }
        return false;
    }

    public static int createClaim(PlayerEntity owner, ItemStack stack) {
        return createClaim(owner.world.getLevelProperties(), stack.getName(), owner.getUuid());
    }

    public static int createClaim(WorldProperties properties, Text name, UUID owner) {
        ClaimListComponent claimComponent = (ClaimListComponent) ComponentManager.CLAIM_LIST.get(properties);
        System.out.println(name.asString() + " "+ owner.toString());
        return claimComponent.addClaim(name, owner);
    }

}
