package io.github.jupiterio.necessaries.tp;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;
import io.github.jupiterio.volcanolib.text.TextBuilder;
import io.github.jupiterio.necessaries.ComponentManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.server.world.ServerWorld;
import io.github.jupiterio.necessaries.warp.SelfWarpsComponent;
import io.github.jupiterio.necessaries.warp.Warp;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class HomeCommand {
    private static final SimpleCommandExceptionType HOME_NOT_SET_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("pcd.warps.home.missing"));
    private static final SimpleCommandExceptionType WRONG_DIMENSION_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("pcd.warps.home.set.fail"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> root = dispatcher.register(
            literal("home")
                .executes(context -> executeGo(context))
                .then(literal("set")
                        .executes(context -> executeSet(context)))
                .then(literal("clear")
                        .executes(context -> executeClear(context)))
        );
    }

    public static int executeGo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        SelfWarpsComponent playerWarps = (SelfWarpsComponent) ComponentManager.SELF_WARPS.get(player);

        Warp homeWarp = playerWarps.getHome();
        if (homeWarp != null) {
            ServerWorld world = source.getMinecraftServer().getWorld(homeWarp.getDimension());
            BlockPos pos = homeWarp.getPos();
            player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, homeWarp.getDirection().asRotation(), 0);

            source.sendFeedback(TextBuilder.builder()
                .translate("pcd.warps.home.go")
                .yellow()
                .build(), false);
        } else {
            throw HOME_NOT_SET_EXCEPTION.create();
        }

        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        SelfWarpsComponent playerWarps = (SelfWarpsComponent) ComponentManager.SELF_WARPS.get(player);

        Vec3d pos = source.getPosition();
        BlockPos blockPos = new BlockPos(pos.x, pos.y+0.1, pos.z);

        boolean success = playerWarps.setHome(new LiteralText("Home"), source.getWorld().getRegistryKey(), blockPos, Direction.fromRotation(source.getRotation().y));

        if (success) {
            source.sendFeedback(TextBuilder.builder()
                .translate("pcd.warps.home.set.success")
                .yellow()
                .build(), false);
        } else {
            throw WRONG_DIMENSION_EXCEPTION.create();
        }

        return 1;
    }

    public static int executeClear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        SelfWarpsComponent playerWarps = (SelfWarpsComponent) ComponentManager.SELF_WARPS.get(player);

        boolean success = playerWarps.clearHome();

        if (success) {
            source.sendFeedback(TextBuilder.builder()
                .translate("pcd.warps.home.clear.success")
                .yellow()
                .build(), false);
        } else {
            throw HOME_NOT_SET_EXCEPTION.create();
        }

        return 1;
    }
}
