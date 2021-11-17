package io.github.jupiterio.necessaries.claim;

import io.github.jupiterio.necessaries.ComponentManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import java.util.List;

public class ChunkClaimComponent extends BaseClaimComponent {
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

    public Chunk getChunk() {
        return this.chunk;
    }
}
