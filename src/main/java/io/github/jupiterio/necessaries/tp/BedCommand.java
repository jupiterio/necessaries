package io.github.jupiterio.necessaries.tp;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;
import io.github.jupiterio.necessaries.builder.TextBuilder;
import io.github.jupiterio.necessaries.ComponentManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.server.world.ServerWorld;
import io.github.jupiterio.necessaries.warp.SelfWarpsComponent;
import io.github.jupiterio.necessaries.warp.Warp;
import io.github.jupiterio.necessaries.builder.TextBuilder;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class BedCommand {
    private static final SimpleCommandExceptionType BED_MISSING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("pcd.warps.bed.missing"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> root = dispatcher.register(
            literal("bed")
                .executes(context -> executeGo(context))
        );
    }

    public static int executeGo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        BlockPos blockPos = player.getSpawnPointPosition();
        RegistryKey<World> dimension = player.getSpawnPointDimension();

        if (blockPos != null && dimension != null) {
            ServerWorld world = source.getMinecraftServer().getWorld(dimension);

            Vec3d pos;
            try {
                pos = (Vec3d)PlayerEntity.findRespawnPosition(world, blockPos, player.isSpawnPointSet(), true).get();
                player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0);
            } catch(Exception e) {
                player.teleport(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
            }

            source.sendFeedback(TextBuilder.builder()
                .translate("pcd.warps.bed.go")
                .yellow()
                .build(), false);
        } else {
            throw BED_MISSING_EXCEPTION.create();
        }

        return 1;
    }
}
