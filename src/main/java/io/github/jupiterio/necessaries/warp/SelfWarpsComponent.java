package io.github.jupiterio.necessaries.warp;

import dev.onyxstudios.cca.api.v3.component.Component;
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
