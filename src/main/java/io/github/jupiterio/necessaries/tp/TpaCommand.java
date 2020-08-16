package io.github.jupiterio.necessaries.tp;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;
import io.github.jupiterio.necessaries.builder.TextBuilder;
import io.github.jupiterio.necessaries.tp.TeleportManager.TeleportType;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static io.github.jupiterio.necessaries.tp.TeleportManager.TeleportType;

public class TpaCommand {
    private static final SimpleCommandExceptionType NONE_PENDING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("pcd.tp.none_pending"));
    private static final DynamicCommandExceptionType ACCEPT_FAILED_EXCEPTION = new DynamicCommandExceptionType(target -> new TranslatableText("pcd.tp.accepted.fail", target));
    private static final DynamicCommandExceptionType REJECT_FAILED_EXCEPTION = new DynamicCommandExceptionType(target -> new TranslatableText("pcd.tp.rejected.fail", target));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> root = dispatcher.register(
            literal("tpa")
                .then(literal("go")
                    .then(argument("target", EntityArgumentType.player())
                        .executes(context -> executeRequest(context, TeleportType.GO, EntityArgumentType.getPlayer(context, "target")))))
                .then(literal("bring")
                    .then(argument("target", EntityArgumentType.player())
                        .executes(context -> executeRequest(context, TeleportType.HERE, EntityArgumentType.getPlayer(context, "target")))))
                .then(literal("accept")
                    .executes(context -> executeAccept(context, null))
                    .then(argument("target", EntityArgumentType.player())
                        .executes(context -> executeAccept(context, EntityArgumentType.getPlayer(context, "target")))))
                .then(literal("reject")
                    .executes(context -> executeReject(context, null))
                    .then(argument("target", EntityArgumentType.player())
                        .executes(context -> executeReject(context, EntityArgumentType.getPlayer(context, "target")))))
                .then(literal("cancel")
                    .executes(context -> executeCancel(context)))
        );
    }

    public static int executeRequest(CommandContext<ServerCommandSource> context, TeleportType tptype, ServerPlayerEntity target) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        try {
            executeCancel(context);
        } catch(CommandSyntaxException e) {
            // cancel failed, ignore
        }

        boolean success = TeleportManager.addRequest(tptype, player, target, type -> (from, to) -> {
            if (type == TeleportType.GO) {
                from.sendSystemMessage(TextBuilder.builder()
                    .translate("pcd.tp.go", to.getDisplayName())
                    .yellow()
                    .literal(" ")
                    .translate("pcd.tp.button.cancel")
                    .red()
                    .command("/tpa cancel")
                    .build(), from.getUuid());

                to.sendSystemMessage(TextBuilder.builder()
                    .translate("pcd.tp.go.other", from.getDisplayName())
                    .gold()
                    .literal(" ")
                    .translate("pcd.tp.button.accept")
                    .aqua()
                    .command("/tpa accept " + from.getName().asString())
                    .literal(" ")
                    .translate("pcd.tp.button.reject")
                    .aqua()
                    .command("/tpa reject " + from.getName().asString())
                    .build(), from.getUuid());
            } else {
                from.sendSystemMessage(TextBuilder.builder()
                    .translate("pcd.tp.bring", to.getDisplayName())
                    .yellow()
                    .literal(" ")
                    .translate("pcd.tp.button.cancel")
                    .red()
                    .command("/tpa cancel")
                    .build(), from.getUuid());

                to.sendSystemMessage(TextBuilder.builder()
                    .translate("pcd.tp.bring.other", from.getDisplayName())
                    .lightPurple()
                    .literal(" ")
                    .translate("pcd.tp.button.accept")
                    .aqua()
                    .command("/tpa accept " + from.getName().asString())
                    .literal(" ")
                    .translate("pcd.tp.button.reject")
                    .aqua()
                    .command("/tpa reject " + from.getName().asString())
                    .build(), from.getUuid());
            }
        });

        return 1;
    }

    public static int executeAccept(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        boolean success = TeleportManager.acceptRequest(player, target, type -> (from, to) -> {
            to.sendSystemMessage(TextBuilder.builder()
                .translate("pcd.tp.accepted", from.getDisplayName())
                .yellow()
                .build(), to.getUuid());

            from.sendSystemMessage(TextBuilder.builder()
                .translate("pcd.tp.accepted.other", to.getDisplayName())
                .yellow()
                .build(), to.getUuid());
        });

        if(!success) {
            if (target == null) {
                throw NONE_PENDING_EXCEPTION.create();
            } else {
                throw ACCEPT_FAILED_EXCEPTION.create(target.getDisplayName());
            }
        }

        return 1;
    }

    public static int executeReject(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        boolean success = TeleportManager.rejectRequest(player, target, type -> (from, to) -> {
            to.sendSystemMessage(TextBuilder.builder()
                .translate("pcd.tp.rejected", from.getDisplayName())
                .red()
                .build(), to.getUuid());

            from.sendSystemMessage(TextBuilder.builder()
                .translate("pcd.tp.rejected.other", to.getDisplayName())
                .red()
                .build(), to.getUuid());
        });

        if(!success) {
            if (target == null) {
                throw NONE_PENDING_EXCEPTION.create();
            } else {
                throw REJECT_FAILED_EXCEPTION.create(target.getDisplayName());
            }
        }

        return 1;
    }

    public static int executeCancel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        boolean success = TeleportManager.cancelRequest(player, type -> (from, to) -> {
            from.sendSystemMessage(TextBuilder.builder()
                .translate("pcd.tp.cancelled", to.getDisplayName())
                .red()
                .build(), from.getUuid());

            to.sendSystemMessage(TextBuilder.builder()
                .translate("pcd.tp.cancelled.other", from.getDisplayName())
                .red()
                .build(), from.getUuid());
        });

        if(!success) throw NONE_PENDING_EXCEPTION.create();

        return 1;
    }
}
