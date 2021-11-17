package io.github.jupiterio.necessaries.warp;

import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class BaseSelfWarpsComponent implements SelfWarpsComponent {
    protected Warp homeWarp;
    private final PlayerEntity player;

    public BaseSelfWarpsComponent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public Warp getHome() { return this.homeWarp; }

    @Override
    public boolean setHome(Text name, RegistryKey<World> world, BlockPos pos, Direction direction) {
        if (world == World.OVERWORLD || world == World.NETHER) {
            this.homeWarp = new Warp(name, world, pos, direction);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean clearHome() {
        if (this.homeWarp != null) {
            this.homeWarp = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.homeWarp = null;
        if (tag.contains("Home", 10)) {
            Warp homeWarp = new Warp();
            homeWarp.fromTag(tag.getCompound("Home"));
            this.homeWarp = homeWarp;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (this.homeWarp != null) {
            tag.put("Home", this.homeWarp.toTag(new NbtCompound()));
        }
    }
}
