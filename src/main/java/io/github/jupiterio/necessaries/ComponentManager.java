package io.github.jupiterio.necessaries;

import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.util.Identifier;
import net.minecraft.entity.player.PlayerEntity;
import io.github.jupiterio.necessaries.claim.ClaimListComponent;
import io.github.jupiterio.necessaries.claim.BaseClaimListComponent;
import io.github.jupiterio.necessaries.claim.ClaimComponent;
import io.github.jupiterio.necessaries.claim.ChunkClaimComponent;
import io.github.jupiterio.necessaries.claim.PlayerClaimComponent;
import io.github.jupiterio.necessaries.warp.WarpListComponent;
import io.github.jupiterio.necessaries.warp.BaseWarpListComponent;
import io.github.jupiterio.necessaries.warp.SelfWarpsComponent;
import io.github.jupiterio.necessaries.warp.BaseSelfWarpsComponent;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public final class ComponentManager implements ChunkComponentInitializer, LevelComponentInitializer, EntityComponentInitializer {
    public static final ComponentKey<ClaimListComponent> CLAIM_LIST =
        ComponentRegistry.getOrCreate(new Identifier("necessaries:claim_list"), ClaimListComponent.class);
    public static final ComponentKey<ClaimComponent> CLAIM =
        ComponentRegistry.getOrCreate(new Identifier("necessaries:claim"), ClaimComponent.class);
    public static final ComponentKey<WarpListComponent> WARP_LIST =
        ComponentRegistry.getOrCreate(new Identifier("necessaries:warp_list"), WarpListComponent.class);
    public static final ComponentKey<SelfWarpsComponent> SELF_WARPS =
        ComponentRegistry.getOrCreate(new Identifier("necessaries:self_warps"), SelfWarpsComponent.class);

    @Override
    public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
        registry.register(CLAIM_LIST, properties -> new BaseClaimListComponent());
        registry.register(WARP_LIST, properties -> new BaseWarpListComponent());
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(CLAIM, ChunkClaimComponent::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, CLAIM, PlayerClaimComponent::new);
        registry.registerFor(PlayerEntity.class, SELF_WARPS, BaseSelfWarpsComponent::new);

        registry.setRespawnCopyStrategy(CLAIM, RespawnCopyStrategy.ALWAYS_COPY);
        registry.setRespawnCopyStrategy(SELF_WARPS, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
