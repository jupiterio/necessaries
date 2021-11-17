package io.github.jupiterio.necessaries.warp;

import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

public class Warp {

    private Text name;
    private RegistryKey<World> dimension;
    private BlockPos pos;
    private Direction direction;

    public Warp(Text name, RegistryKey<World> dimension, BlockPos pos, Direction direction) {
        this.name = name;
        this.dimension = dimension;
        this.pos = pos;
        this.direction = direction;
    }

    public Warp() {
        this(new LiteralText(""), World.OVERWORLD, new BlockPos(0, 0, 0), Direction.NORTH);
    }

    public Text getName() {
        return this.name;
    }

    public RegistryKey<World> getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void fromTag(NbtCompound tag) {
        this.name = Text.Serializer.fromJson(tag.getString("Name"));
        this.dimension = RegistryKey.of(Registry.WORLD_KEY, new Identifier(tag.getString("Dimension")));
        this.pos = NbtHelper.toBlockPos(tag.getCompound("Pos"));
        this.direction = Direction.fromRotation(tag.getFloat("Rotation"));
    }

    public NbtCompound toTag(NbtCompound tag) {
        tag.putString("Name", Text.Serializer.toJson(this.name));
        tag.putString("Dimension", this.dimension.getValue().toString());
        tag.put("Pos", NbtHelper.fromBlockPos(this.pos));
        tag.putFloat("Rotation", this.direction.asRotation());

        return tag;
    }
}
