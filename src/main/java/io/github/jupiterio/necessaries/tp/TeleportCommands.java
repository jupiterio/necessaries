package io.github.jupiterio.necessaries.tp;

import net.fabricmc.fabric.api.registry.CommandRegistry;

public class TeleportCommands {

    public static void initialize() {
        System.out.println("Registering Necessaries Commands");

        CommandRegistry.INSTANCE.register(false, TpaCommand::register);
        CommandRegistry.INSTANCE.register(false, HomeCommand::register);
        CommandRegistry.INSTANCE.register(false, WarpCommand::register);
        CommandRegistry.INSTANCE.register(false, BedCommand::register);
        CommandRegistry.INSTANCE.register(false, SpawnCommand::register);
    }
}
