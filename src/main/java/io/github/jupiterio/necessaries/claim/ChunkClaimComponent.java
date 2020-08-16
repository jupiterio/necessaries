package io.github.jupiterio.necessaries.claim;

import io.github.jupiterio.necessaries.ComponentManager;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import nerdhub.cardinal.components.api.util.ChunkComponent;
import java.util.List;

public class ChunkClaimComponent extends BaseClaimComponent implements ChunkComponent<BaseClaimComponent> {
    private final Chunk chunk;

    public ChunkClaimComponent(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public Claim getClaimData() {
        if (this.chunk instanceof WorldChunk) {
            WorldChunk chunk = (WorldChunk) this.chunk;

            BaseClaimListComponent claimComponent = (BaseClaimListComponent) ComponentManager.CLAIM_LIST.get(chunk.getWorld().getLevelProperties());

            return claimComponent.getClaim(this.id);
        } else {
            return Claim.UNKNOWN;
        }
    }

    @Override
    public ComponentType<ClaimComponent> getComponentType() {
        return (ComponentType<ClaimComponent>) ComponentManager.CLAIM;
    }

    public Chunk getChunk() {
        return this.chunk;
    }
}
