package io.github.jupiterio.necessaries.warp;

import nerdhub.cardinal.components.api.component.Component;
import java.util.LinkedHashMap;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface WarpListComponent extends Component {
    LinkedHashMap<String, Warp> getWarps();
    void setWarp(String id, Text name, RegistryKey<World> world, BlockPos pos, Direction direction);
    Warp getWarp(String id);
    void removeWarp(String id);
}
