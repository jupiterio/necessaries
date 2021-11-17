package io.github.jupiterio.necessaries.warp;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
    public void readFromNbt(NbtCompound tag) {
        this.warps.clear();

        NbtList warpList = tag.getList("Warps", 10);
        NbtList idList = tag.getList("Ids", 8);

        for (int i=0; i<warpList.size(); i++) {
            Warp warp = new Warp();
            warp.fromTag(warpList.getCompound(i));

            this.warps.put(idList.getString(i), warp);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        Iterator warpsIter = this.warps.entrySet().iterator();
        NbtList warpList = new NbtList();
        NbtList idList = new NbtList();

        while(warpsIter.hasNext()) {
            Map.Entry warpEntry = (Map.Entry) warpsIter.next();

            warpList.add(((Warp)warpEntry.getValue()).toTag(new NbtCompound()));
            idList.add(NbtString.of((String)warpEntry.getKey()));
        }

        tag.put("Warps", warpList);
        tag.put("Ids", idList);
    }
}
