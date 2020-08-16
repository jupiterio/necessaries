package io.github.jupiterio.necessaries.claim;

import io.github.jupiterio.necessaries.ComponentManager;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import dev.onyxstudios.cca.api.v3.util.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;

public class PlayerClaimComponent extends BaseClaimComponent implements PlayerComponent<BaseClaimComponent> {
    private final PlayerEntity player;

    public PlayerClaimComponent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public Claim getClaimData() {
        BaseClaimListComponent claimComponent = (BaseClaimListComponent) ComponentManager.CLAIM_LIST.get(this.player.world.getLevelProperties());

        return claimComponent.getClaim(this.id);
    }

    @Override
    public ComponentType<ClaimComponent> getComponentType() {
        return (ComponentType<ClaimComponent>) ComponentManager.CLAIM;
    }

    @Override
    public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory) {
        return true;
    }

    @Override
    public void copyForRespawn(BaseClaimComponent original, boolean lossless, boolean keepInventory) {
        PlayerComponent.super.copyForRespawn(original, lossless, keepInventory);
    }
}
