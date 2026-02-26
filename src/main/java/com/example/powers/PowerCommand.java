package com.example.powers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PowerCommand implements CommandExecutor, TabCompleter {
    private final PowerManager powerManager;

    public PowerCommand(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("power.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /power <add|remove|awaken|list> <power> <player>");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("list")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /power list <player>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            sender.sendMessage(powerManager.summary(target));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /power " + sub + " <power> <player>");
            return true;
        }

        PowerType power = PowerType.fromInput(args[1]).orElse(null);
        if (power == null) {
            sender.sendMessage(ChatColor.RED + "Unknown power. Try: " +
                Arrays.stream(PowerType.values()).map(PowerType::key).collect(Collectors.joining(", ")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        switch (sub) {
            case "add" -> {
                if (powerManager.addPower(target, power)) {
                    sender.sendMessage(ChatColor.GREEN + "Added " + power.displayName() + " to " + target.getName() + ".");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + target.getName() + " already has that power.");
                }
            }
            case "remove" -> {
                if (powerManager.removePower(target, power)) {
                    sender.sendMessage(ChatColor.GREEN + "Removed " + power.displayName() + " from " + target.getName() + ".");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + target.getName() + " does not have that power.");
                }
            }
            case "awaken" -> {
                if (powerManager.awaken(target, power)) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + " awakened " + power.displayName() + "!");
                    target.sendMessage(ChatColor.LIGHT_PURPLE + "Your " + power.displayName() + " has awakened!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Cannot awaken. Check ownership, mastery (100%), or if already awakened.");
                }
            }
            default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
        }

        powerManager.save();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return partial(args[0], List.of("add", "remove", "awaken", "list"));
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                return partial(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            }
            return partial(args[1], Arrays.stream(PowerType.values()).map(PowerType::key).toList());
        }
        if (args.length == 3 && !args[0].equalsIgnoreCase("list")) {
            return partial(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }
        return List.of();
    }

    private List<String> partial(String token, List<String> values) {
        List<String> matches = new ArrayList<>();
        String lower = token.toLowerCase(Locale.ROOT);
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(lower)) {
                matches.add(value);
            }
        }
        return matches;
    }
}
