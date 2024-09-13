package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.util.Unico;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EkidenCmd extends Cmd {
    public EkidenCmd(com.github.nosh11.ekidensys.EkidenSys plugin) {
        super(plugin);
    }

    @Override
    Cmd getInstance() {
        return this;
    }

    @Override
    public String getCommandName() {
        return "ekiden";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "test_unicode" -> {
                    if (args.length < 2) return false;
                    sender.sendMessage(Unico.convert(args[1]));
                }
                case "reload" -> {
                    ApiManager.getInstance().reloadAll();
                    sender.sendMessage("Reload completed");
                }
            }
        }
        return false;
    }
}
