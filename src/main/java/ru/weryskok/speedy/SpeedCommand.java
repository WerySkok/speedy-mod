package ru.weryskok.speedy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class SpeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("speed")
                .requires(Permissions.require("speedy.speed", 2))
                .then(CommandManager.argument("speed", FloatArgumentType.floatArg(0.0001f, 10.0f))
                    .then(CommandManager.argument("type", StringArgumentType.word())
                        .suggests(new MovementTypeSuggestionProvider())
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .requires(Permissions.require("speedy.speed.others", 2))
                            .executes(ctx -> speed(ctx.getSource(),
                                FloatArgumentType.getFloat(ctx, "speed"),
                                StringArgumentType.getString(ctx, "type"),
                                EntityArgumentType.getPlayer(ctx, "player"))))
                        .executes(ctx -> speed(ctx.getSource(),
                            FloatArgumentType.getFloat(ctx, "speed"),
                            StringArgumentType.getString(ctx, "type"),
                            null)))
                    .executes(ctx -> speed(ctx.getSource(),
                        FloatArgumentType.getFloat(ctx, "speed"), null, null))));
    }

    public static int speed(ServerCommandSource source, float speed, String type, PlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            player = source.getPlayer();
        }
        if (type == null) {
            type = player.getAbilities().flying ? "flight" : "walk";
        }
        if (type.equals("flight")) {
            player.getAbilities().setFlySpeed(0.05f * speed);
        } else {
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1f * speed);
        }
        player.sendAbilitiesUpdate();
        source.sendFeedback(Text.literal(String.format("Set %s's %s speed to %.2f.",
                player.getName().getString(),
                type.equals("flight") ? "flight" : "walking",
                speed)), false);
        return 0;
    }
}
