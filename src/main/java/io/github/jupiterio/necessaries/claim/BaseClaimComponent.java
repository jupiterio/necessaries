package io.github.jupiterio.necessaries.claim;

import io.github.jupiterio.necessaries.ComponentManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
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
    public void readFromNbt(NbtCompound tag) {
        this.id = tag.getInt("id");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("id", this.id);
    }
}
