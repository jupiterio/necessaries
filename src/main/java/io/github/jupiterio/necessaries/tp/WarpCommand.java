package io.github.jupiterio.necessaries.tp;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import io.github.jupiterio.volcanolib.text.TextBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.text.Text;
import io.github.jupiterio.necessaries.warp.WarpListComponent;
import io.github.jupiterio.necessaries.warp.Warp;
import io.github.jupiterio.necessaries.ComponentManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.TextArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class WarpCommand {
//     private static final SimpleCommandExceptionType NOT_LIVING_EXCEPTION = new SimpleCommandExceptionType(new LiteralText("You can only use this command with mobs and players"));

    private static final Pattern nonAlphanumeric = Pattern.compile("[^a-zA-Z0-9]");
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, suggestionsBuilder) -> {
        ServerCommandSource source = context.getSource();
        WarpListComponent warpsComponent = (WarpListComponent) ComponentManager.WARP_LIST.get(source.getWorld().getLevelProperties());

        Set<String> warpIds = warpsComponent.getWarps().keySet();

        return CommandSource.suggestMatching(warpIds.stream().map(s->{
            if (nonAlphanumeric.matcher(s).find()) {
                return '"'+s+'"';
            } else {
                return s;
            }
        }), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> root = dispatcher.register(
            literal("warp")
                .executes(context -> executeList(context))
                .then(literal("go")
                    .then(argument("name", StringArgumentType.string()).suggests(SUGGESTION_PROVIDER)
                        .executes(context -> executeGo(context, StringArgumentType.getString(context, "name")))))
                .then(literal("set")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("name", StringArgumentType.string()).suggests(SUGGESTION_PROVIDER)
                        .executes(context -> executeSet(context, StringArgumentType.getString(context, "name"), null))
                        .then(argument("displayName", TextArgumentType.text())
                            .executes(context -> executeSet(context, StringArgumentType.getString(context, "name"), TextArgumentType.getTextArgument(context, "displayName"))))))
                .then(literal("remove")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("name", StringArgumentType.string()).suggests(SUGGESTION_PROVIDER)
                        .executes(context -> executeRemove(context, StringArgumentType.getString(context, "name")))))
        );
    }

    public static int executeList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        player.sendSystemMessage(TextBuilder.builder()
            .translate("pcd.warps.title")
            .bold()
            .build(), player.getUuid());

        player.sendSystemMessage(TextBuilder.builder()
            .translate("pcd.warps.custom.spawn")
            .darkAqua()
            .command("/spawn")
            .build(), player.getUuid());

        WarpListComponent warpsComponent = (WarpListComponent) ComponentManager.WARP_LIST.get(source.getWorld().getLevelProperties());

        LinkedHashMap<String, Warp> warps = warpsComponent.getWarps();

        Iterator warpsIter = warps.entrySet().iterator();

        while(warpsIter.hasNext()) {
            Map.Entry warpEntry = (Map.Entry) warpsIter.next();

            String name = (String)warpEntry.getKey();
            Text displayName = ((Warp)warpEntry.getValue()).getName();

            player.sendSystemMessage(TextBuilder.builder()
                .text(displayName)
                .command("/warp go \"" + name + '"')
                .build(), player.getUuid());
        }

        player.sendSystemMessage(TextBuilder.builder()
            .translate("pcd.warps.home")
            .aqua()
            .command("/home")
            .text(" (")
            .translate("pcd.warps.home.set")
            .aqua()
            .command("/home set")
            .text(") (")
            .translate("pcd.warps.home.clear")
            .aqua()
            .command("/home clear")
            .text(")")
            .build(), player.getUuid());

        player.sendSystemMessage(TextBuilder.builder()
            .translate("pcd.warps.bed")
            .aqua()
            .command("/bed")
            .build(), player.getUuid());

        List<ServerPlayerEntity> players = source.getMinecraftServer().getPlayerManager().getPlayerList();
        Iterator playersIter = players.iterator();

        if (players.size() > 1) player.sendSystemMessage(TextBuilder.builder()
            .text("----")
            .bold()
            .build(), player.getUuid());

        while(playersIter.hasNext()) {
            ServerPlayerEntity other = (ServerPlayerEntity) playersIter.next();

            if (player != other) {
                player.sendSystemMessage(TextBuilder.builder()
                    .translate("pcd.tp.button.go")
                    .green()
                    .command("/tpa go " + other.getName().asString())
                    .text(" ")
                    .translate("pcd.tp.button.bring")
                    .green()
                    .command("/tpa bring " + other.getName().asString())
                    .text(" - ")
                    .text(other.getDisplayName())
                    .build(), player.getUuid());
            }
        }

        return 1;
    }

    public static int executeGo(CommandContext<ServerCommandSource> context, String id) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        WarpListComponent warpsComponent = (WarpListComponent) ComponentManager.WARP_LIST.get(source.getWorld().getLevelProperties());
        Warp warp = warpsComponent.getWarp(id);

        if (warp != null) {
            ServerWorld world = source.getMinecraftServer().getWorld(warp.getDimension());
            BlockPos pos = warp.getPos();
            player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, warp.getDirection().asRotation(), 0);

            source.sendFeedback(TextBuilder.builder()
                .translate("pcd.warps.custom.go", warp.getName())
                .yellow()
                .build(), false);
        } else {
            // TODO: throw error
        }

        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context, String id, Text displayName) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        WarpListComponent warpsComponent = (WarpListComponent) ComponentManager.WARP_LIST.get(source.getWorld().getLevelProperties());

        Vec3d pos = source.getPosition();
        BlockPos blockPos = new BlockPos(pos.x, pos.y+0.1, pos.z);

        if (displayName == null) {
            Warp warp = warpsComponent.getWarp(id);
            if (warp != null) {
                displayName = warp.getName();
            } else {
                // TODO: throw error
                return 1;
            }
        }

        warpsComponent.setWarp(id, displayName, source.getWorld().getRegistryKey(), blockPos, Direction.fromRotation(source.getRotation().y));

        return 1;
    }

    public static int executeRemove(CommandContext<ServerCommandSource> context, String id) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        WarpListComponent warpsComponent = (WarpListComponent) ComponentManager.WARP_LIST.get(source.getWorld().getLevelProperties());

        warpsComponent.removeWarp(id);

        return 1;
    }
}
