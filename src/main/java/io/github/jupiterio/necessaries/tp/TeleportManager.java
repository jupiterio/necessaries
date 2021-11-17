package io.github.jupiterio.necessaries.tp;

import com.google.common.collect.Lists;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.PlayerManager;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.Iterator;
import net.minecraft.server.world.ServerWorld;
import java.util.function.Function;
import java.util.function.BiConsumer;
import io.github.jupiterio.volcanolib.text.TextBuilder;

public class TeleportManager {
    private static List<TpaRequest> pending = Lists.newArrayList();

    public static boolean addRequest(TeleportType type, ServerPlayerEntity from, ServerPlayerEntity to, Function<TeleportType, BiConsumer<ServerPlayerEntity, ServerPlayerEntity>> callback) {
        if (pending.stream().anyMatch(f -> f.from == from)) {
            return false;
        }

        pending.add(new TpaRequest(type, from, to));
        callback.apply(type).accept(from, to);

        return true;
    }

    public static boolean acceptRequest(ServerPlayerEntity accepter, ServerPlayerEntity sender, Function<TeleportType, BiConsumer<ServerPlayerEntity, ServerPlayerEntity>> callback) {
        Iterator reqIter = pending.stream().filter(f -> f.from == sender && (accepter == null || f.to == accepter)).iterator();

        if (!reqIter.hasNext()) return false;

        while(reqIter.hasNext()) {
            TpaRequest req = (TpaRequest) reqIter.next();

            req.execute();
            callback.apply(req.type).accept(req.from, req.to);
        }

        return true;
    }

    public static boolean rejectRequest(ServerPlayerEntity rejecter, ServerPlayerEntity sender, Function<TeleportType, BiConsumer<ServerPlayerEntity, ServerPlayerEntity>> callback) {
        Iterator reqIter = pending.stream().filter(f -> f.from == sender && (rejecter == null || f.to == rejecter)).iterator();

        if (!reqIter.hasNext()) return false;

        while(reqIter.hasNext()) {
            TpaRequest req = (TpaRequest) reqIter.next();

            req.stop();
            callback.apply(req.type).accept(req.from, req.to);
        }

        return true;
    }

    public static boolean cancelRequest(ServerPlayerEntity canceler, Function<TeleportType, BiConsumer<ServerPlayerEntity, ServerPlayerEntity>> callback) {
        TpaRequest req = pending.stream().filter(f -> f.from == canceler).findFirst().orElse(null);

        if (req != null) {
            req.stop();
            callback.apply(req.type).accept(req.from, req.to);
            return true;
        } else {
            return false;
        }
    }

    public static enum TeleportType {
        GO,
        HERE
    }

    public static class TpaRequest {
        public final TeleportType type;
        public final ServerPlayerEntity from;
        public final ServerPlayerEntity to;
        private Timer timer;

        public TpaRequest(TeleportType type, ServerPlayerEntity from, ServerPlayerEntity to) {
            this.type = type;
            this.from = from;
            this.to = to;

            this.timer = new Timer();

            TpaRequest req = this;
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TeleportManager.pending.remove(req);

                    req.to.sendSystemMessage(TextBuilder.builder()
                        .translate("pcd.tp.expired.other", req.from.getDisplayName())
                        .red()
                        .build(), req.from.getUuid());

                    req.from.sendSystemMessage(TextBuilder.builder()
                        .translate("pcd.tp.expired", req.to.getDisplayName())
                        .red()
                        .build(), req.from.getUuid());
                }
            }, (long)30*1000);
        }

        public void stop() {
            TeleportManager.pending.remove(this);
            this.timer.cancel();
        }

        public void execute() {
            if (this.type == TeleportType.GO) {
                this.from.teleport((ServerWorld)this.to.world, this.to.getX(), this.to.getY(), this.to.getZ(), this.to.getYaw(), this.to.getPitch());
            } else {
                this.to.teleport((ServerWorld)this.from.world, this.from.getX(), this.from.getY(), this.from.getZ(), this.from.getYaw(), this.from.getPitch());
            }
            this.stop();
        }
    }
}
