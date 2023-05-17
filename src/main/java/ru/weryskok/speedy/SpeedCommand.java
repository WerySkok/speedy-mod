package ru.weryskok.speedy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class SpeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("speed")
                        .requires(Permissions.require("speedy.speed", 2))
                        .then(
                                argument("speed", floatArg(0.0001f, 10f))
                                        .then(
                                                argument("type", word())
                                                        .suggests(new MovementTypeSuggestionProvider())
                                                        .then(
                                                                argument("player", player())
                                                                        .requires(Permissions.require("speedy.speed.others", 2))
                                                                        .executes(ctx -> speed(ctx.getSource(), FloatArgumentType.getFloat(ctx, "speed"), StringArgumentType.getString(ctx, "type"), EntityArgumentType.getPlayer(ctx, "player")))
                                                        )
                                                        .executes(ctx -> speed(ctx.getSource(), FloatArgumentType.getFloat(ctx, "speed"), StringArgumentType.getString(ctx, "type"), null))
                                        )
                                        .executes(ctx -> speed(ctx.getSource(), FloatArgumentType.getFloat(ctx, "speed"), null, null))
                        )
        );
    }

    public static int speed(ServerCommandSource source, float speed, String type, PlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            player = source.getPlayer();
        }
        if (type == null) {
            if (player.getAbilities().flying) {
                type = "flight";
            } else {
                type = "walk";
            }
        }

        if (type.equals("flight")) {
            player.getAbilities().setFlySpeed(0.05f * speed);
        } else {
            // player.getAbilities().setWalkSpeed(0.1f * speed);
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1f * speed);
        }
        player.sendAbilitiesUpdate();

        source.sendFeedback(new LiteralText(String.format("Set %s's %s speed to %.2f.", player.getName().getString(), type.equals("flight") ? "flight" : "walking", speed)), false);

        return 0;
    }
}
