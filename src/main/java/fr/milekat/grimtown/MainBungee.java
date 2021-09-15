package fr.milekat.grimtown;

import dev.morphia.Datastore;
import fr.milekat.grimtown.event.ThisEvent;
import fr.milekat.grimtown.proxy.ProxyManager;
import fr.milekat.grimtown.utils.ConfigManager;
import fr.milekat.grimtown.utils.MongoDB;
import fr.milekat.grimtown.utils.RabbitMQ;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.util.HashMap;

public class MainBungee extends Plugin {
    //  Bungee/configs
    private static MainBungee mainBungee;
    private Configuration config;
    public static String PREFIX = "§7[§bEVENT§7]§r ";
    public static boolean DEBUG_ERRORS = false;

    /* MongoDB */
    private static HashMap<String, Datastore> datastoreMap = new HashMap<>();
    /* Rabbit */
    public static boolean DEBUG_RABBIT = false;
    /* Event */
    private static ThisEvent thisEvent;

    @Override
    public void onEnable(){
        /* Bungee configs */
        mainBungee = this;
        ConfigManager data = new ConfigManager(this);
        config = data.getConfigs();
        data.loadConfigs();
        /* MongoDB */
        datastoreMap = MongoDB.getDatastoreMap(config);
        /* RabbitMQ */
        try {
            new RabbitMQ().getRabbitConsumer().start();
        } catch (IOException exception) {
            warning("RabbitMQ consumer error ! RabbitMQ disabled");
            exception.printStackTrace();
        }
        /* Master load */
        new ProxyManager(this, ProxyServer.getInstance().getPluginManager());
        /* Event load */
        thisEvent = new ThisEvent();
        if (DEBUG_ERRORS) log("Debugs enable, plugin loaded");
    }

    @Override
    public void onDisable(){
        //sql.disconnect();
    }

    public static MainBungee getInstance(){ return mainBungee; }

    public static void log(String message) { ProxyServer.getInstance().getLogger().info(MainBungee.PREFIX + message); }
    public static void info(String log) { ProxyServer.getInstance().getLogger().info(MainBungee.PREFIX + log); }
    public static void warning(String log) { ProxyServer.getInstance().getLogger().warning(MainBungee.PREFIX + log); }

    public static Configuration getConfig() { return mainBungee.config; }

    public static ThisEvent getThisEvent() { return thisEvent; }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    public static Datastore getDatastore(String dbName) {
        return datastoreMap.get(dbName).startSession();
    }
}
