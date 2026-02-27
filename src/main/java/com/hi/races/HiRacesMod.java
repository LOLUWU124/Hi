package com.hi.races;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HiRacesMod implements ModInitializer {
    private static final String[] RACE_NAMES = {"giller", "nightling", "metaljaw", "fireborn", "swiftling", "phantom"};

    private static final Map<UUID, PhantomState> PHANTOM_STATES = new HashMap<>();

    @Override
    public void onInitialize() {
        registerCommands();
        registerTickLogic();
        registerMetalJawLogic();
        registerFirebornCombatLogic();
        registerPhantomBodyDeathWatcher();
    }

    private void registerCommands() {
        SuggestionProvider<ServerCommandSource> raceSuggestions = (context, builder) ->
                CommandSource.suggestMatching(RACE_NAMES, builder);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("race")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("racename", StringArgumentType.greedyString())
                                        .suggests(raceSuggestions)
                                        .executes(this::executeRaceCommand)
                                ))
        ));
    }

    private int executeRaceCommand(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        String raceName = StringArgumentType.getString(context, "racename").trim().replace("_", "").replace("-", "").replace(" ", "");

        Race race = Race.fromString(raceName).orElseGet(() -> Race.autoCorrect(raceName));
        if (race == null || race == Race.NONE) {
            context.getSource().sendError(Text.literal("Unknown race. Valid races: giller, nightling, metaljaw, fireborn, swiftling, phantom"));
            return 0;
        }

        RacePersistentState state = RacePersistentState.get(context.getSource().getServer());
        state.setRace(target.getUuid(), race);
        clearRaceEffects(target);

        context.getSource().sendFeedback(() -> Text.literal("Set " + target.getName().getString() + " to race " + race.id()), true);
        target.sendMessage(Text.literal("Your race is now " + race.id() + "."), false);
        return 1;
    }

    private void registerTickLogic() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            RacePersistentState state = RacePersistentState.get(server);
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Race race = state.getRace(player.getUuid());
                switch (race) {
                    case GILLER -> handleGiller(player);
                    case NIGHTLING -> handleNightling(player);
                    case FIREBORN -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 220, 0, true, false));
                    case SWIFTLING -> handleSwiftling(player);
                    case PHANTOM -> handlePhantomToggle(player);
                    default -> {
                    }
                }
            }
        });
    }

    private void handleGiller(ServerPlayerEntity player) {
        if (player.isSubmergedInWater()) {
            player.setAir(player.getMaxAir());
            if (player.isSwimming()) {
                Vec3d velocity = player.getVelocity();
                player.setVelocity(velocity.x * 1.7D, velocity.y, velocity.z * 1.7D);
                player.velocityModified = true;
            }
            return;
        }

        int tickStep = 1;
        if (player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
            tickStep += 2;
        }

        int respirationLevel = EnchantmentHelper.getRespiration(player);
        if (respirationLevel > 0) {
            tickStep += respirationLevel;
        }

        if (player.age % tickStep == 0) {
            player.setAir(player.getAir() - 1);
            if (player.getAir() <= -20) {
                player.setAir(0);
                player.damage(player.getDamageSources().drown(), 2.0F);
            }
        }
    }

    private void handleNightling(ServerPlayerEntity player) {
        if (player.getWorld().isNight()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 220, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 220, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, true, false));
        } else {
            player.removeStatusEffect(StatusEffects.SPEED);
            player.removeStatusEffect(StatusEffects.STRENGTH);
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    private void handleSwiftling(ServerPlayerEntity player) {
        if (player.isSprinting()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 3, true, false));
        } else {
            player.removeStatusEffect(StatusEffects.SPEED);
        }
    }

    private void registerMetalJawLogic() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            Race race = RacePersistentState.get(serverPlayer.getServer()).getRace(serverPlayer.getUuid());
            if (race != Race.METAL_JAW) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty() || stack.isFood()) {
                return TypedActionResult.pass(stack);
            }

            if (!player.getHungerManager().isNotFull()) {
                return TypedActionResult.fail(stack);
            }

            stack.decrement(1);
            player.getHungerManager().add(2, 0.2F);
            world.playSound(null, player.getBlockPos(), net.minecraft.sound.SoundEvents.ENTITY_GENERIC_EAT, player.getSoundCategory(), 1.0F, 1.0F);
            return TypedActionResult.success(player.getStackInHand(hand));
        });
    }

    private void registerFirebornCombatLogic() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(source.getAttacker() instanceof ServerPlayerEntity attacker) || blocked) {
                return;
            }

            Race race = RacePersistentState.get(attacker.getServer()).getRace(attacker.getUuid());
            if (race != Race.FIREBORN) {
                return;
            }

            ItemStack main = attacker.getMainHandStack();
            if (main.isEmpty()) {
                entity.setOnFireFor(4);
                return;
            }

            if (main.getItem() instanceof SwordItem || main.getItem() instanceof ToolItem) {
                entity.setOnFireFor(8);
            }
        });
    }

    private void handlePhantomToggle(ServerPlayerEntity player) {
        boolean sneaking = player.isSneaking();
        PhantomState existing = PHANTOM_STATES.get(player.getUuid());

        if (!sneaking) {
            if (existing != null) {
                existing.crouchReleased = true;
            }
            return;
        }

        if (existing == null) {
            PhantomState state = new PhantomState();
            state.returnPos = player.getPos();
            state.worldKey = player.getWorld().getRegistryKey();
            state.bodyId = spawnBody(player).getUuid();
            state.crouchReleased = false;
            PHANTOM_STATES.put(player.getUuid(), state);

            player.changeGameMode(GameMode.SPECTATOR);
            player.sendMessage(Text.literal("Phantom form active. Crouch again to return."), true);
            return;
        }

        if (existing.crouchReleased) {
            exitPhantom(player, false);
        }
    }

    private ArmorStandEntity spawnBody(ServerPlayerEntity player) {
        ArmorStandEntity body = new ArmorStandEntity(player.getWorld(), player.getX(), player.getY(), player.getZ());
        body.setCustomName(Text.literal(player.getName().getString() + "'s body"));
        body.setCustomNameVisible(true);
        body.setNoGravity(true);
        player.getWorld().spawnEntity(body);
        return body;
    }

    private void registerPhantomBodyDeathWatcher() {
        ServerTickEvents.END_SERVER_TICK.register(server -> PHANTOM_STATES.entrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) {
                return true;
            }

            PhantomState state = entry.getValue();
            ServerWorld world = server.getWorld(state.worldKey);
            if (world == null) {
                return true;
            }

            if (world.getEntity(state.bodyId) == null) {
                if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                    player.changeGameMode(GameMode.SURVIVAL);
                }
                player.damage(player.getDamageSources().genericKill(), Float.MAX_VALUE);
                player.sendMessage(Text.literal("Your body was destroyed while you were a phantom."), false);
                return true;
            }

            return false;
        }));
    }

    private void clearRaceEffects(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.SPEED);
        player.removeStatusEffect(StatusEffects.STRENGTH);
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);

        exitPhantom(player, true);
    }

    private void exitPhantom(ServerPlayerEntity player, boolean silent) {
        PhantomState state = PHANTOM_STATES.remove(player.getUuid());
        if (state == null) {
            return;
        }

        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            player.changeGameMode(GameMode.SURVIVAL);
        }

        ServerWorld world = player.getServer().getWorld(state.worldKey);
        if (world != null) {
            var body = world.getEntity(state.bodyId);
            if (body != null) {
                body.discard();
            }
            player.teleport(world, state.returnPos.x, state.returnPos.y, state.returnPos.z, player.getYaw(), player.getPitch());
        }

        if (!silent) {
            player.sendMessage(Text.literal("Returned to your body."), true);
        }
    }

    private static class PhantomState {
        Vec3d returnPos;
        net.minecraft.registry.RegistryKey<World> worldKey;
        UUID bodyId;
        boolean crouchReleased;
    }
}
