// Main class: VIPAccess.java
package by.t1pe.vIPAccess;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Logger;


public class VIPAccess extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private File configFile;
    private File logFile;
    private FileConfiguration logConfig;
    private Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);

        // Проверка на null для команды
        if (getCommand("vipaccess") != null) {
            Objects.requireNonNull(getCommand("vipaccess")).setExecutor(new VIPAccessCommand(this));
        } else {
            logger.severe("Команда 'vipaccess' не найдена в plugin.yml!");
            getServer().getPluginManager().disablePlugin(this); // Используем disablePlugin вместо setEnabled
        }
    }

    private void loadConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        logFile = new File(getDataFolder(), "logs.yml");
        logConfig = YamlConfiguration.loadConfiguration(logFile);
    }

    public void reloadPlugin() {
        reloadConfig();
        loadConfig();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!config.getBoolean("enabled", true)) return;

        Player player = event.getPlayer();
        String permission = config.getString("required-permission", "server.vip");

        if (!player.hasPermission(permission)) {
            String kickMessage = config.getString("kick-message", "Нет доступа!");
            kickMessage = translateAlternateColorCodes('&', '§', kickMessage);
            // Используем Component вместо устаревшего setKickMessage
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.kickMessage(LegacyComponentSerializer.legacySection().deserialize(kickMessage));

            // Logging
            String logEntry = String.format(
                    "time: %s, player: %s, uuid: %s, ip: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    player.getName(),
                    player.getUniqueId(),
                    event.getAddress().getHostAddress()
            );
            logConfig.set("logs.entry-" + System.currentTimeMillis(), logEntry);
            saveLog();

            // Notify admins
            String adminMessage = String.format(
                    "Игрок %s (UUID: %s) пытался войти без прав! IP: %s",
                    player.getName(), player.getUniqueId(), event.getAddress().getHostAddress()
            );
            adminMessage = translateAlternateColorCodes('&', '§', adminMessage);
            String finalAdminMessage = adminMessage;
            getServer().getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("vipaccess.admin"))
                    .forEach(p -> p.sendMessage(finalAdminMessage));
        }
    }

    // Метод для преобразования кодов форматирования
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

    private void saveLog() {
        try {
            logConfig.save(logFile);
        } catch (IOException e) {
            logger.severe("Не удалось сохранить лог: " + e.getMessage());
        }
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @NotNull
    public FileConfiguration getLogConfig() {
        return logConfig;
    }

    public void saveConfigFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            logger.severe("Не удалось сохранить конфиг: " + e.getMessage());
        }
    }
}