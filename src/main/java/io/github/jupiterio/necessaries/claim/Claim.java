package io.github.jupiterio.necessaries.claim;

import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;

public class Claim {
    public static Claim WILDERNESS;
    public static Claim UNKNOWN;
    private Text name;
    private UUID owner;

    public Claim(Text name, UUID owner) {
        this.name = name;
        this.owner = owner;
    }

    public Claim() {
        this(new LiteralText(""), Util.NIL_UUID);
    }

    public Text getName() {
        return this.name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void fromTag(CompoundTag tag) {
        this.name = Text.Serializer.fromJson(tag.getString("Name"));
        this.owner = tag.getUuid("Owner");
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("Name", Text.Serializer.toJson(this.name));
        tag.putUuid("Owner", this.owner);
        return tag;
    }

    static {
        LiteralText wilderness = new LiteralText("Wilderness");
        wilderness.setStyle(wilderness.getStyle().withColor(Formatting.DARK_GREEN));
        WILDERNESS = new Claim(wilderness, Util.NIL_UUID);

        LiteralText unknown = new LiteralText("missingno");
        unknown.setStyle(unknown.getStyle().withColor(Formatting.DARK_RED));
        UNKNOWN = new Claim(unknown, Util.NIL_UUID);
    }
}
