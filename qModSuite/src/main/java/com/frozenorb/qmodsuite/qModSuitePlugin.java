package com.frozenorb.qmodsuite;

import com.frozenorb.commonlibs.ConfigHelper;
import com.frozenorb.commonlibs.redis.RedisCredentials;
import com.frozenorb.commonlibs.redis.RedisHelper;
import com.frozenorb.qmodsuite.commands.*;
import com.frozenorb.qmodsuite.listeners.ProfileListeners;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;


public class qModSuitePlugin extends JavaPlugin {

    private static boolean setup = false;
    @Getter private static RedisHelper redisHelper;
    @Getter private static ModSuitePubSub pubSub;
    @Getter private static ConfigHelper config;

    @Override
    public void onEnable() {
        if (!setup){
            config = new ConfigHelper(this,"config", getDataFolder().getAbsolutePath());
            pubSub = new ModSuitePubSub();
            redisHelper = new RedisHelper(new RedisCredentials(config.getConfiguration().getString("Redis.IP"),
                    config.getConfiguration().getString("Redis.Password"),
                    config.getConfiguration().getInt("Redis.Port")));
        }
        /* Subscribe to the Pub Sub Channels */
        pubSub.subscribe(ModSuitePubSub.IDENTIFIER + "SC",
                ModSuitePubSub.IDENTIFIER + "RQ",
                ModSuitePubSub.IDENTIFIER + "RP");
        /* Register Commands and Plugin Listeners */
        registerCommands();
        registerListeners();
        setup = true;
    }

    @Override
    public void onDisable() {
        /* Unsubscribe from the Pub Sub Channels */
        pubSub.unsubscribe();
    }

    /**
     * Register Commands
     */
    private void registerCommands(){
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("request").setExecutor(new RequestCommand());
        getCommand("staffchat").setExecutor(new StaffChatCommand());
        getCommand("staffmode").setExecutor(new StaffModeCommand());
        getCommand("staffonline").setExecutor(new StaffOnlineCommand());
    }

    /**
     * Register Listeners
     */
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ProfileListeners(), this);
    }
}
