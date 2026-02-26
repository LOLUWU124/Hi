package com.example.powers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PowerListener implements Listener {
    private final PowerManager powerManager;
    private final Random random = new Random();
    private final Map<UUID, Long> throttle = new HashMap<>();

    public PowerListener(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material type = event.getItem().getType();

        if (has(player, PowerType.METAL_JAW)) {
            powerManager.addMastery(player, PowerType.METAL_JAW, masteryFromRarity(type));
            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
            pulse(player, Particle.CLOUD, 10);
            pulse(player, Particle.CRIT, 6);
        }
        if (has(player, PowerType.IRON_HEART)) {
            powerManager.addMastery(player, PowerType.IRON_HEART, 1.0);
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 0.5));
            pulse(player, Particle.HEART, 4);
        }
        if (has(player, PowerType.PHOENIX_SOUL) && (type == Material.GOLDEN_APPLE || type == Material.ENCHANTED_GOLDEN_APPLE)) {
            powerManager.addMastery(player, PowerType.PHOENIX_SOUL, 1.8);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, true, false, true));
            pulse(player, Particle.FLAME, 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMetalJawUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!has(player, PowerType.METAL_JAW)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || item.getType().isEdible()) return;

        Material consumed = item.getType();
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);

        powerManager.addMastery(player, PowerType.METAL_JAW, masteryFromRarity(consumed));
        player.sendMessage(ChatColor.GRAY + "Metal Jaw consumed " + consumed.name().toLowerCase().replace('_', ' ') + ".");
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
        pulse(player, Particle.CLOUD, 10);
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        if (now - throttle.getOrDefault(player.getUniqueId(), 0L) < 800) return;
        throttle.put(player.getUniqueId(), now);

        if (has(player, PowerType.SKY_SENTINEL)) {
            enableHeroFlight(player);
            powerManager.addMastery(player, PowerType.SKY_SENTINEL, 0.35);
            if (awakened(player, PowerType.SKY_SENTINEL)) {
                ring(player, Particle.CLOUD, 10);
                player.setFlyingFallDamage(false);
                base(player, PowerType.SKY_SENTINEL, PotionEffectType.SPEED, 1, 45);
            }
        }

        if (player.isSprinting()) {
            base(player, PowerType.DUNE_STRIDER, PotionEffectType.SPEED, 0, 45);
            base(player, PowerType.BLAZE_RUNNER, PotionEffectType.SPEED, 0, 45);
            base(player, PowerType.SOLAR_MONK, PotionEffectType.HASTE, 0, 45);
            powerManager.addMastery(player, PowerType.DUNE_STRIDER, 0.5);
            powerManager.addMastery(player, PowerType.BLAZE_RUNNER, 0.45);
            powerManager.addMastery(player, PowerType.SOLAR_MONK, 0.4);
            if (awakened(player, PowerType.BLAZE_RUNNER)) {
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 8, 0.3, 0.1, 0.3, 0.01);
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.2f);
            }
        }

        if (player.isSwimming()) {
            base(player, PowerType.OCEAN_WARDEN, PotionEffectType.DOLPHINS_GRACE, 0, 50);
            powerManager.addMastery(player, PowerType.OCEAN_WARDEN, 0.6);
        }

        if (player.isSneaking()) {
            base(player, PowerType.SHADOW_SPECTER, PotionEffectType.INVISIBILITY, 0, 30);
            base(player, PowerType.MIRROR_CLOAK, PotionEffectType.INVISIBILITY, 0, 30);
            powerManager.addMastery(player, PowerType.SHADOW_SPECTER, 0.45);
            powerManager.addMastery(player, PowerType.MIRROR_CLOAK, 0.45);
        }

        if (player.isGliding()) {
            base(player, PowerType.AETHER_WING, PotionEffectType.SLOW_FALLING, 0, 60);
            powerManager.addMastery(player, PowerType.AETHER_WING, 0.7);
            powerManager.addMastery(player, PowerType.STARFORGED, 0.4);
        }

        base(player, PowerType.BEACON_SPIRIT, PotionEffectType.NIGHT_VISION, 0, 90);
        powerManager.addMastery(player, PowerType.BEACON_SPIRIT, 0.2);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();

        if ((type == Material.OBSIDIAN || type == Material.CRYING_OBSIDIAN) && has(player, PowerType.OBSIDIAN_SPINE)) {
            powerManager.addMastery(player, PowerType.OBSIDIAN_SPINE, 1.8);
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 0, true, false, true));
            pulse(player, Particle.CLOUD, 10);
        }
        if ((type == Material.QUARTZ_ORE || type == Material.NETHER_QUARTZ_ORE || type == Material.AMETHYST_BLOCK) && has(player, PowerType.CRYSTAL_ARCHER)) {
            powerManager.addMastery(player, PowerType.CRYSTAL_ARCHER, 1.4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 60, 0, true, false, true));
            pulse(player, Particle.ENCHANT, 8);
        }
        if ((type == Material.NETHERRACK || type == Material.BLACKSTONE || type == Material.MAGMA_BLOCK) && has(player, PowerType.NETHER_REAVER)) {
            powerManager.addMastery(player, PowerType.NETHER_REAVER, 1.3);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false, true));
            pulse(player, Particle.FLAME, 8);
        }
        if ((type == Material.SCULK || type == Material.SCULK_SENSOR || type == Material.SCULK_CATALYST) && has(player, PowerType.WARDEN_PULSE)) {
            powerManager.addMastery(player, PowerType.WARDEN_PULSE, 1.7);
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, 0, true, false, true));
        }
        if ((type == Material.REDSTONE_ORE || type == Material.DEEPSLATE_REDSTONE_ORE || type == Material.COPPER_ORE) && has(player, PowerType.GEARMIND)) {
            powerManager.addMastery(player, PowerType.GEARMIND, 1.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 50, 0, true, false, true));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        Entity target = event.getEntity();

        powerManager.addMastery(player, PowerType.ARC_PUNCHER, 0.6);
        powerManager.addMastery(player, PowerType.RAVAGER_STANCE, 0.7);
        powerManager.addMastery(player, PowerType.RIFT_BLADE, 0.6);

        if (has(player, PowerType.ARC_PUNCHER) && random.nextDouble() < 0.12) {
            event.setDamage(event.getDamage() + 1.0);
            pulse(player, Particle.ELECTRIC_SPARK, 10);
        }
        if (has(player, PowerType.RAVAGER_STANCE)) {
            event.setDamage(event.getDamage() * 1.08);
        }
        if (has(player, PowerType.THUNDERCLAP) && random.nextDouble() < 0.15) {
            event.setDamage(event.getDamage() + 1.5);
            powerManager.addMastery(player, PowerType.THUNDERCLAP, 0.8);
            if (awakened(player, PowerType.THUNDERCLAP)) {
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation(), 20, 0.4, 0.6, 0.4, 0.02);
                event.setDamage(event.getDamage() + 2.0);
            }
        }
        if (has(player, PowerType.LUNAR_ASSASSIN) && player.getWorld().getTime() > 13000) {
            event.setDamage(event.getDamage() + 1.0);
            powerManager.addMastery(player, PowerType.LUNAR_ASSASSIN, 0.9);
            if (awakened(player, PowerType.LUNAR_ASSASSIN)) {
                event.setDamage(event.getDamage() + 1.5);
            }
        }
        if (has(player, PowerType.STORM_LANCER) && player.getWorld().hasStorm()) {
            event.setDamage(event.getDamage() + 1.2);
            powerManager.addMastery(player, PowerType.STORM_LANCER, 1.0);
        }
        if (has(player, PowerType.PHASE_STEPPER)) {
            powerManager.addMastery(player, PowerType.PHASE_STEPPER, 0.6);
            if (random.nextDouble() < 0.10) {
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 18, 0.4, 0.6, 0.4, 0.05);
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 0.4f, 1.5f);
            }
        }
        if (has(player, PowerType.VOID_WALKER)) {
            powerManager.addMastery(player, PowerType.VOID_WALKER, 0.5);
        }
        if (has(player, PowerType.GRAVITY_KNIGHT) && target instanceof LivingEntity living) {
            powerManager.addMastery(player, PowerType.GRAVITY_KNIGHT, 0.7);
            living.setVelocity(living.getVelocity().multiply(0.6));
            if (awakened(player, PowerType.GRAVITY_KNIGHT)) {
                living.setVelocity(new Vector(0, 0.2, 0));
            }
        }
        if (has(player, PowerType.STARFORGED) && player.getFallDistance() > 2.5) {
            powerManager.addMastery(player, PowerType.STARFORGED, 0.9);
            event.setDamage(event.getDamage() + 1.2);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (has(player, PowerType.TITAN_GUARD)) {
            powerManager.addMastery(player, PowerType.TITAN_GUARD, Math.min(2, event.getFinalDamage() * 0.15));
            event.setDamage(event.getDamage() * 0.94);
            if (awakened(player, PowerType.TITAN_GUARD)) {
                event.setDamage(event.getDamage() * 0.75);
            }
        }
        if (has(player, PowerType.BULWARK)) {
            powerManager.addMastery(player, PowerType.BULWARK, 0.7);
            event.setDamage(event.getDamage() * 0.95);
        }
        if (has(player, PowerType.PRISMATIC_HIDE)) {
            powerManager.addMastery(player, PowerType.PRISMATIC_HIDE, 0.8);
            event.setDamage(event.getDamage() * 0.96);
        }
        if (has(player, PowerType.THORN_GUARDIAN) && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            powerManager.addMastery(player, PowerType.THORN_GUARDIAN, 0.9);
        }
        if (has(player, PowerType.EARTHSHAKER) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            powerManager.addMastery(player, PowerType.EARTHSHAKER, 1.0);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 14, 0.5, 0.1, 0.5, 0.01);
            ring(player, Particle.CLOUD, 8);
        }
        if (has(player, PowerType.PHOENIX_SOUL)
                && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR)) {
            powerManager.addMastery(player, PowerType.PHOENIX_SOUL, 1.2);
            event.setDamage(event.getDamage() * 0.75);
            if (awakened(player, PowerType.PHOENIX_SOUL)) {
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1.0));
                pulse(player, Particle.FLAME, 12);
            }
        }
        if (has(player, PowerType.TOXIC_VEIL) && event.getCause() == EntityDamageEvent.DamageCause.POISON) {
            powerManager.addMastery(player, PowerType.TOXIC_VEIL, 1.1);
            event.setDamage(event.getDamage() * 0.6);
        }
        if (has(player, PowerType.RIFT_BLADE) && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            powerManager.addMastery(player, PowerType.RIFT_BLADE, 0.8);
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.08, 0)));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onXp(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        if (has(player, PowerType.GEARMIND)) {
            powerManager.addMastery(player, PowerType.GEARMIND, Math.min(2.0, event.getAmount() * 0.08));
            if (awakened(player, PowerType.GEARMIND)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 80, 1, true, false, true));
            }
        }
        if (has(player, PowerType.LIGHT_BRINGER)) {
            powerManager.addMastery(player, PowerType.LIGHT_BRINGER, Math.min(2.2, event.getAmount() * 0.09));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, true, false, true));
            pulse(player, Particle.END_ROD, 6);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnimal(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals)) return;
        Player player = event.getPlayer();
        if (has(player, PowerType.WILD_CALLER)) {
            powerManager.addMastery(player, PowerType.WILD_CALLER, 1.4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 70, 0, true, false, true));
            pulse(player, Particle.HEART, 6);
        }
        if (has(player, PowerType.BEEKEEPER)) {
            powerManager.addMastery(player, PowerType.BEEKEEPER, 1.5);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 70, 0, true, false, true));
            pulse(player, Particle.HEART, 6);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLightTool(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        Material inHand = player.getInventory().getItemInMainHand().getType();
        if ((inHand == Material.TORCH || inHand == Material.LANTERN || inHand == Material.SOUL_LANTERN) && has(player, PowerType.LANTERN_GAZE)) {
            powerManager.addMastery(player, PowerType.LANTERN_GAZE, 0.8);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, true, false, true));
            pulse(player, Particle.END_ROD, 6);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvisibilityGate(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getModifiedType() == PotionEffectType.INVISIBILITY && !awakened(player, PowerType.MIRROR_CLOAK) && !has(player, PowerType.MIRROR_CLOAK)) {
            event.setCancelled(true);
        }
    }

    private void enableHeroFlight(Player player) {
        if (!player.getAllowFlight() && player.getGameMode().name().equals("SURVIVAL")) {
            player.setAllowFlight(true);
        }
    }

    private boolean has(Player player, PowerType type) {
        return powerManager.hasPower(player.getUniqueId(), type);
    }

    private boolean awakened(Player player, PowerType type) {
        PowerState state = powerManager.getState(player, type);
        return state != null && state.isAwakened();
    }


    private void base(Player player, PowerType type, PotionEffectType effectType, int amplifier, int ticks) {
        if (has(player, type)) {
            player.addPotionEffect(new PotionEffect(effectType, ticks, amplifier, true, false, true));
        }
    }


    private void pulse(Player player, Particle particle, int count) {
        player.getWorld().spawnParticle(particle, player.getLocation().add(0, 1, 0), count, 0.35, 0.45, 0.35, 0.01);
    }

    private void ring(Player player, Particle particle, int points) {
        for (int i = 0; i < points; i++) {
            double a = (Math.PI * 2 / points) * i;
            double x = Math.cos(a) * 0.8;
            double z = Math.sin(a) * 0.8;
            player.getWorld().spawnParticle(particle, player.getLocation().add(x, 0.2, z), 1, 0, 0, 0, 0);
        }
    }

    private double masteryFromRarity(Material material) {
        String name = material.name();
        if (name.contains("NETHERITE") || name.contains("DIAMOND") || name.contains("ANCIENT_DEBRIS")) return 5.5;
        if (name.contains("IRON") || name.contains("GOLD") || name.contains("EMERALD")) return 3.8;
        if (name.contains("STONE") || name.contains("COPPER")) return 2.4;
        return 1.6;
    }
}
