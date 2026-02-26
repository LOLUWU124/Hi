package com.example.powers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PowerCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS = List.of("add", "remove", "awaken", "list");

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

        String rawSub = args[0].toLowerCase(Locale.ROOT);
        String sub = autocorrectSubcommand(rawSub).orElse(rawSub);
        if (!rawSub.equals(sub)) {
            sender.sendMessage(ChatColor.GRAY + "Auto-corrected subcommand '" + rawSub + "' -> '" + sub + "'.");
        }

        if (sub.equals("list")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /power list <player>");
                return true;
            }
            Player target = findPlayerWithAutocorrect(sender, args[1]);
            if (target == null) {
                return true;
            }
            sender.sendMessage(powerManager.summary(target));
            return true;
        }

        if (!SUBCOMMANDS.contains(sub)) {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand. Try: " + String.join(", ", SUBCOMMANDS));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /power " + sub + " <power> <player>");
            return true;
        }

        PowerType power = parsePowerWithAutocorrect(sender, args[1]);
        if (power == null) {
            return true;
        }

        Player target = findPlayerWithAutocorrect(sender, args[2]);
        if (target == null) {
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
            return partial(args[0], SUBCOMMANDS);
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

    private PowerType parsePowerWithAutocorrect(CommandSender sender, String input) {
        Optional<PowerType> exact = PowerType.fromInput(input);
        if (exact.isPresent()) {
            return exact.get();
        }

        List<String> keys = Arrays.stream(PowerType.values()).map(PowerType::key).toList();
        String corrected = closest(input, keys).orElse(null);
        if (corrected != null) {
            sender.sendMessage(ChatColor.GRAY + "Auto-corrected power '" + input + "' -> '" + corrected + "'.");
            return PowerType.fromInput(corrected).orElse(null);
        }

        sender.sendMessage(ChatColor.RED + "Unknown power. Try: " + String.join(", ", keys));
        return null;
    }

    private Optional<String> autocorrectSubcommand(String input) {
        if (SUBCOMMANDS.contains(input)) {
            return Optional.of(input);
        }
        return closest(input, SUBCOMMANDS);
    }

    private Player findPlayerWithAutocorrect(CommandSender sender, String input) {
        Player exact = Bukkit.getPlayerExact(input);
        if (exact != null) {
            return exact;
        }

        List<String> online = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        String corrected = closest(input, online).orElse(null);
        if (corrected == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return null;
        }

        Player correctedPlayer = Bukkit.getPlayerExact(corrected);
        if (correctedPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return null;
        }

        sender.sendMessage(ChatColor.GRAY + "Auto-corrected player '" + input + "' -> '" + correctedPlayer.getName() + "'.");
        return correctedPlayer;
    }

    private Optional<String> closest(String input, List<String> values) {
        if (values.isEmpty()) {
            return Optional.empty();
        }
        String lower = input.toLowerCase(Locale.ROOT);
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String value : values) {
            int dist = levenshtein(lower, value.toLowerCase(Locale.ROOT));
            if (dist < bestDistance) {
                bestDistance = dist;
                best = value;
            }
        }
        return bestDistance <= 2 ? Optional.of(best) : Optional.empty();
    }

    private int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] curr = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }
        return prev[b.length()];
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
