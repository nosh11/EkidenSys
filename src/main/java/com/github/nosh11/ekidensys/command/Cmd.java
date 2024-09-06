package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public abstract class Cmd implements CommandExecutor {
    private static EkidenSys plugin;
    public Cmd(EkidenSys plugin) {
        if (this.getPlugin() == null) this.setPlugin(plugin);
        if (this.getInstance() == null) throw new NullPointerException("Instance is null");
        if (this.getCommandName() == null) throw new NullPointerException("Command name is null");
        this.register();
    }
    final EkidenSys getPlugin() {
        return Cmd.plugin;
    }
    final void setPlugin(EkidenSys instance) {
        if (instance == null)
            throw new IllegalArgumentException("Command gg");
        Cmd.plugin = instance;
    }
    public void register() {
        PluginCommand c = this.getPlugin().getCommand(this.getCommandName());
        if (c != null) {
            c.setExecutor(this.getInstance());
            if (this.getInstance() instanceof TabCompleter)
                c.setTabCompleter((TabCompleter) this.getInstance());
        }
    }
    abstract Cmd getInstance();
    public abstract String getCommandName();
}