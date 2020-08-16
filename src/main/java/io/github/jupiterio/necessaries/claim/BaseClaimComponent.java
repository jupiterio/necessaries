package io.github.jupiterio.necessaries.claim;

import io.github.jupiterio.necessaries.ComponentManager;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import nerdhub.cardinal.components.api.util.ChunkComponent;
import java.util.List;

public class BaseClaimComponent implements ClaimComponent {
    protected int id = 0;

    @Override
    public int getId() { return this.id; }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public Claim getClaimData() {
        return null;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.id = tag.getInt("id");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("id", this.id);
        return tag;
    }
}
