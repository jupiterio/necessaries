package io.github.jupiterio.necessaries.warp;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BaseWarpListComponent implements WarpListComponent {
    private LinkedHashMap<String, Warp> warps = Maps.newLinkedHashMap();

    @Override
    public LinkedHashMap<String, Warp> getWarps() {
        return this.warps;
    }

    @Override
    public void setWarp(String id, Text name, RegistryKey<World> world, BlockPos pos, Direction direction) {
        this.warps.put(id, new Warp(name, world, pos, direction));
    }

    @Override
    public Warp getWarp(String id) {
        return this.warps.get(id);
    }

    @Override
    public void removeWarp(String id) {
        this.warps.remove(id);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.warps.clear();

        ListTag warpList = tag.getList("Warps", 10);
        ListTag idList = tag.getList("Ids", 8);

        for (int i=0; i<warpList.size(); i++) {
            Warp warp = new Warp();
            warp.fromTag(warpList.getCompound(i));

            this.warps.put(idList.getString(i), warp);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Iterator warpsIter = this.warps.entrySet().iterator();
        ListTag warpList = new ListTag();
        ListTag idList = new ListTag();

        while(warpsIter.hasNext()) {
            Map.Entry warpEntry = (Map.Entry) warpsIter.next();

            warpList.add(((Warp)warpEntry.getValue()).toTag(new CompoundTag()));
            idList.add(StringTag.of((String)warpEntry.getKey()));
        }

        tag.put("Warps", warpList);
        tag.put("Ids", idList);

        return tag;
    }
}
