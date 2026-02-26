package com.example.powersmod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PowersMod implements ModInitializer {
    private static final PowerManager MANAGER = new PowerManager();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
            CommandManager.literal("power")
                .requires(src -> src.hasPermissionLevel(2))
                .then(CommandManager.literal("list")
                    .then(CommandManager.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayerEntity target = findPlayer(ctx.getSource(), StringArgumentType.getString(ctx, "player"));
                            if (target == null) return 0;
                            ctx.getSource().sendFeedback(() -> Text.literal(MANAGER.summary(target)), false);
                            return 1;
                        })))
                .then(CommandManager.literal("add")
                    .then(CommandManager.argument("power", StringArgumentType.word())
                        .then(CommandManager.argument("player", StringArgumentType.word())
                            .executes(ctx -> executePower(ctx.getSource(), "add",
                                StringArgumentType.getString(ctx, "power"),
                                StringArgumentType.getString(ctx, "player"))))))
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("power", StringArgumentType.word())
                        .then(CommandManager.argument("player", StringArgumentType.word())
                            .executes(ctx -> executePower(ctx.getSource(), "remove",
                                StringArgumentType.getString(ctx, "power"),
                                StringArgumentType.getString(ctx, "player"))))))
                .then(CommandManager.literal("awaken")
                    .then(CommandManager.argument("power", StringArgumentType.word())
                        .then(CommandManager.argument("player", StringArgumentType.word())
                            .executes(ctx -> executePower(ctx.getSource(), "awaken",
                                StringArgumentType.getString(ctx, "power"),
                                StringArgumentType.getString(ctx, "player"))))))
        ));
    }

    private int executePower(ServerCommandSource source, String action, String powerInput, String playerInput) {
        ServerPlayerEntity player = findPlayer(source, playerInput);
        if (player == null) return 0;

        PowerType power = PowerType.fromInput(powerInput).orElse(null);
        if (power == null) {
            source.sendError(Text.literal("Unknown power: " + powerInput));
            return 0;
        }

        boolean result = switch (action) {
            case "add" -> MANAGER.add(player, power);
            case "remove" -> MANAGER.remove(player, power);
            case "awaken" -> MANAGER.awaken(player, power);
            default -> false;
        };

        if (!result) {
            source.sendError(Text.literal("Action failed."));
            return 0;
        }

        source.sendFeedback(() -> Text.literal("Success: " + action + " " + power.key() + " for " + player.getName().getString()), true);
        return 1;
    }

    private ServerPlayerEntity findPlayer(ServerCommandSource source, String name) {
        ServerPlayerEntity p = source.getServer().getPlayerManager().getPlayer(name);
        if (p == null) source.sendError(Text.literal("Player not found: " + name));
        return p;
    }
}
