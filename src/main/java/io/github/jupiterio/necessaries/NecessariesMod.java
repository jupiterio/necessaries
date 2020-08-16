package io.github.jupiterio.necessaries;

import net.fabricmc.api.ModInitializer;
import io.github.jupiterio.necessaries.claim.ClaimManager;
import io.github.jupiterio.necessaries.tp.TeleportCommands;
import io.github.jupiterio.necessaries.portal.PortalManager;

public class NecessariesMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        System.out.println("Initializing Necessaries");

        ClaimManager.initialize();
        TeleportCommands.initialize();
        PortalManager.initialize();
    }
}
