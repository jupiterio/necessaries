package io.github.jupiterio.necessaries.warp;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface SelfWarpsComponent extends Component {
    Warp getHome();
    boolean setHome(Text name, RegistryKey<World> world, BlockPos pos, Direction direction);
    boolean clearHome();
}
