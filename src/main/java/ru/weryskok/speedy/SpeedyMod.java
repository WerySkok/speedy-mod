package ru.weryskok.speedy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static ru.weryskok.speedy.SpeedCommand.register;

public class SpeedyMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    // public static final Logger LOGGER = LoggerFactory.getLogger("speedy");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> register(dispatcher));
    }
}
