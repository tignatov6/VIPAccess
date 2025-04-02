// Command class: VIPAccessCommand.java
package by.t1pe.vIPAccess;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VIPAccessCommand implements CommandExecutor, TabCompleter { // Добавляем TabCompleter
    private final VIPAccess plugin;

    public VIPAccessCommand(VIPAccess plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("vipaccess.admin")) {
            sender.sendMessage("Нет прав на использование этой команды!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadPlugin();
                sender.sendMessage("Плагин перезагружен!");
                break;

            case "enabled":
                if (args.length == 1) {
                    sender.sendMessage("Текущий статус: " + plugin.getConfig().getBoolean("enabled"));
                } else {
                    boolean value = Boolean.parseBoolean(args[1]);
                    plugin.getConfig().set("enabled", value);
                    plugin.saveConfigFile();
                    sender.sendMessage("Статус изменен на: " + value);
                }
                break;

            case "required-permission":
                if (args.length == 1) {
                    sender.sendMessage("Текущее право: " + plugin.getConfig().getString("required-permission"));
                } else {
                    plugin.getConfig().set("required-permission", args[1]);
                    plugin.saveConfigFile();
                    sender.sendMessage("Право изменено на: " + args[1]);
                }
                break;

            case "kick-message":
                if (args.length == 1) {
                    sender.sendMessage("Текущее сообщение: " + plugin.getConfig().getString("kick-message"));
                } else {
                    String message = String.join(" ", args).substring(args[0].length() + 1);
                    plugin.getConfig().set("kick-message", message);
                    plugin.saveConfigFile();
                    sender.sendMessage("Сообщение изменено на: " + message);
                }
                break;

            case "stats":
                int attempts = 0;
                if (plugin.getLogConfig().getConfigurationSection("logs") != null) {
                    attempts = Objects.requireNonNull(plugin.getLogConfig().getConfigurationSection("logs")).getKeys(false).size();
                }
                sender.sendMessage("Попыток входа без прав: " + attempts);
                break;

            default:
                sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Проверяем права
        if (!sender.hasPermission("vipaccess.admin")) {
            return completions; // Пустой список, если нет прав
        }

        // Подсказки для первого аргумента
        if (args.length == 1) {
            completions.add("reload");
            completions.add("enabled");
            completions.add("required-permission");
            completions.add("kick-message");
            completions.add("stats");
        }

        // Подсказки для второго аргумента команды enabled
        if (args.length == 2 && args[0].equalsIgnoreCase("enabled")) {
            completions.add("true");
            completions.add("false");
        }

        // Фильтруем подсказки по введённому тексту
        String currentArg = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(currentArg));

        return completions;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(translateAlternateColorCodes('&', '§', "§eИспользование:"));
        sender.sendMessage(translateAlternateColorCodes('&', '§', "§a/vipaccess reload §7- Перезагрузить плагин"));
        sender.sendMessage(translateAlternateColorCodes('&', '§', "§a/vipaccess enabled [true/false] §7- Включить/выключить"));
        sender.sendMessage(translateAlternateColorCodes('&', '§', "§a/vipaccess required-permission [permission] §7- Установить право"));
        sender.sendMessage(translateAlternateColorCodes('&', '§', "§a/vipaccess kick-message [message] §7- Установить сообщение"));
        sender.sendMessage(translateAlternateColorCodes('&', '§', "§a/vipaccess stats §7- Показать статистику"));
    }
    // Добавляем метод translateAlternateColorCodes в этот класс
    private String translateAlternateColorCodes(char altColorChar, char colorChar, String text) {
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = colorChar;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}