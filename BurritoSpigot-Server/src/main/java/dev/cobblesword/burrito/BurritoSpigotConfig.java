package dev.cobblesword.burrito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Throwables;

public class BurritoSpigotConfig {

    private static File CONFIG_FILE;
    public static final String HEADER = "This is the main configuration file for BurritoSpigot.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to BurritoSpigot,\n"
            + "join us in our Discord.\n"
            + "Discord: https://discord.gg/SBTEbSx\n"
            + "Github: https://github.com/CobbleSword/BurritoSpigot\n"
            + "\n"
            + "Options:\n"
            + "warnTooManyChannelsRegistered:\n"
            + "Suggest false if Burrito was lobby server\n"
            + "thread options:\n"
            + "Adjust it base on CPU amounts\n"
            + "customChatLength:\n"
            + "1.8's default was 100, that's not enough if you use lots cmds. Suggest 256 or longer.\n"
            ;
    /*========================================================================*/
    static YamlConfiguration config;
    public static int version;
    /*========================================================================*/

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try {
            System.out.println("Loading BurritoSpigot config from " + configFile.getName());
            config.load(CONFIG_FILE);
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load burrito.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        version = getInt("config-version", 1);
        set("config-version", 1);
        readConfig(BurritoSpigotConfig.class, null);
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw Throwables.propagate(ex.getCause());
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        config.addDefault(path, def);
        return config.getFloat(path, config.getFloat(path));
    }

    @SuppressWarnings("all")
    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    @SuppressWarnings("all")
    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return (List<T>) config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }
    /*========================================================================*/

    public static boolean warnTooManyChannelsRegistered;

    private static void warnTooManyChannelsRegistered(){
        warnTooManyChannelsRegistered = getBoolean("warnTooManyChannelsRegistered", true);
    }

    public static int chunkThreads;
    public static int playersPerThread;

    private static void chunk() {
        chunkThreads = getInt("chunkThreads", 2);
        playersPerThread = getInt("playersPerThread", 50);
    }

    public static int customChatLength;

    private static void customChatLength(){
        customChatLength = getInt("customChatLength", 100);
    }

}
