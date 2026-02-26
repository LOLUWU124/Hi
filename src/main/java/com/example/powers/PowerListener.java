package com.example.powers;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
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

public class PowerListener implements Listener {
    private static final EnumSet<Material> PLANT_BLOCKS = EnumSet.of(
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG, Material.JUNGLE_LOG,
            Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG,
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
            Material.PUMPKIN, Material.MELON, Material.SUGAR_CANE, Material.CACTUS);

    private final PowerManager powerManager;
    private final Map<UUID, Long> moveTickThrottle = new HashMap<>();
    private final Random random = new Random();

    public PowerListener(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEatFood(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (powerManager.hasPower(player.getUniqueId(), PowerType.METAL_JAW)) {
            powerManager.addMastery(player, PowerType.METAL_JAW, masteryFromRarity(event.getItem().getType()));
            if (isAwakened(player, PowerType.METAL_JAW)) {
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 2.0));
                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
            }
        }
        if (powerManager.hasPower(player.getUniqueId(), PowerType.IRON_STOMACH)) {
            powerManager.addMastery(player, PowerType.IRON_STOMACH, 2.0);
            if (isAwakened(player, PowerType.IRON_STOMACH)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 0, true, false, true));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMetalJawInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        if (!powerManager.hasPower(player.getUniqueId(), PowerType.METAL_JAW)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || item.getType().isEdible()) {
            return;
        }

        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);
        powerManager.addMastery(player, PowerType.METAL_JAW, masteryFromRarity(item.getType()));
        player.sendMessage(ChatColor.GRAY + "Metal Jaw consumed " + pretty(item.getType()) + ".");
        if (isAwakened(player, PowerType.METAL_JAW)) {
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 0, true, false, true));
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        long last = moveTickThrottle.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < 700) {
            return;
        }
        moveTickThrottle.put(player.getUniqueId(), now);

        if (player.isSprinting()) {
            powerManager.addMastery(player, PowerType.WIND_STEP, 0.6);
            if (isAwakened(player, PowerType.WIND_STEP) && random.nextDouble() < 0.15) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 1, true, false, true));
            }
        }
        if (player.isSwimming()) {
            powerManager.addMastery(player, PowerType.TIDE_CALLER, 0.6);
            if (isAwakened(player, PowerType.TIDE_CALLER)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 60, 0, true, false, true));
            }
        }
        if (player.isSneaking()) {
            powerManager.addMastery(player, PowerType.SHADOW_VEIL, 0.5);
        }

        Location below = player.getLocation().clone().subtract(0, 1, 0);
        Material under = below.getBlock().getType();
        if (under == Material.SNOW_BLOCK || under == Material.PACKED_ICE || under == Material.BLUE_ICE || under == Material.ICE) {
            powerManager.addMastery(player, PowerType.FROST_BLOOM, 0.8);
        }
        if (player.isGliding()) {
            powerManager.addMastery(player, PowerType.AETHER_SHIFT, 1.0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();
        if (PLANT_BLOCKS.contains(type) || Tag.LEAVES.isTagged(type)) {
            powerManager.addMastery(player, PowerType.NATURE_TOUCH, 1.5);
        }
        if (Tag.BASE_STONE_OVERWORLD.isTagged(type) && player.getWorld().getTime() < 12300) {
            powerManager.addMastery(player, PowerType.SUNFORGE, 0.9);
        }
        if (type == Material.AMETHYST_BLOCK || type == Material.BUDDING_AMETHYST || type == Material.QUARTZ_BLOCK) {
            powerManager.addMastery(player, PowerType.CRYSTAL_MIND, 1.8);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        Entity target = event.getEntity();

        powerManager.addMastery(player, PowerType.FLAME_HEART, 1.0);
        powerManager.addMastery(player, PowerType.THUNDER_PULSE, 0.9);
        powerManager.addMastery(player, PowerType.BLOODRUSH, 1.0);

        long time = player.getWorld().getTime();
        if (time > 13000 && time < 23000) {
            powerManager.addMastery(player, PowerType.MOONLIT_HUNTER, 1.2);
        }
        if (player.getWorld().hasStorm()) {
            powerManager.addMastery(player, PowerType.STORM_EYE, 1.1);
        }

        if (isAwakened(player, PowerType.FLAME_HEART) && target instanceof LivingEntity living) {
            living.setFireTicks(Math.max(living.getFireTicks(), 120));
        }
        if (isAwakened(player, PowerType.THUNDER_PULSE) && random.nextDouble() < 0.20) {
            target.getWorld().strikeLightningEffect(target.getLocation());
            event.setDamage(event.getDamage() + 3.0);
        }
        if (isAwakened(player, PowerType.BLOODRUSH) && player.getHealth() <= 10.0) {
            event.setDamage(event.getDamage() * 1.35);
        }
        if (isAwakened(player, PowerType.GRAVITY_WELL) && random.nextDouble() < 0.25) {
            target.setVelocity(target.getVelocity().multiply(0.2));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        powerManager.addMastery(player, PowerType.STONE_SKIN, Math.min(2.5, event.getFinalDamage() * 0.18));
        powerManager.addMastery(player, PowerType.GRAVITY_WELL, 0.6);

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            powerManager.addMastery(player, PowerType.VOID_GLIDE, 1.2);
            powerManager.addMastery(player, PowerType.AETHER_SHIFT, 0.8);
            if (isAwakened(player, PowerType.VOID_GLIDE)) {
                event.setDamage(event.getDamage() * 0.35);
            }
        }

        if (isAwakened(player, PowerType.STONE_SKIN)) {
            event.setDamage(event.getDamage() * 0.88);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onXp(PlayerExpChangeEvent event) {
        powerManager.addMastery(event.getPlayer(), PowerType.ARCANE_ECHO, Math.min(3.0, event.getAmount() * 0.12));
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnimalInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Animals) {
            powerManager.addMastery(event.getPlayer(), PowerType.BEAST_BOND, 2.4);
            if (isAwakened(event.getPlayer(), PowerType.BEAST_BOND)) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 0, true, false, true));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAwakenedInvisibility(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getModifiedType() == PotionEffectType.INVISIBILITY
                && !isAwakened(player, PowerType.SHADOW_VEIL)) {
            event.setCancelled(true);
        }
    }

    private boolean isAwakened(Player player, PowerType powerType) {
        PowerState state = powerManager.getState(player, powerType);
        return state != null && state.isAwakened();
    }

    private double masteryFromRarity(Material material) {
        if (material.name().contains("NETHERITE") || material.name().contains("DIAMOND") || material.name().contains("ANCIENT_DEBRIS")) {
            return 7.5;
        }
        if (material.name().contains("IRON") || material.name().contains("GOLD") || material.name().contains("EMERALD")) {
            return 5.0;
        }
        if (material.name().contains("STONE") || material.name().contains("COPPER")) {
            return 3.0;
        }
        return 2.0;
    }

    private String pretty(Material material) {
        return material.name().toLowerCase().replace('_', ' ');
    }
}
