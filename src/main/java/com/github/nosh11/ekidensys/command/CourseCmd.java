package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.course.CourseManager;
import com.github.nosh11.ekidensys.runner.Runner;
import com.github.nosh11.ekidensys.runner.RunnerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CourseCmd extends Cmd {
    public CourseCmd(EkidenSys plugin) {
        super(plugin);
    }

    @Override
    Cmd getInstance() {
        return this;
    }

    @Override
    public String getCommandName() {
        return "course";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "list" -> {
                    for (Course course : CourseManager.getInstance().getAll()) {
                        sender.sendMessage(String.format("""
                                        Course ID: %s
                                        Point Size: %d""",
                                course.getId(),
                                course.getAllPoints().size()));
                    }
                }

                case "addpoint" -> {
                    if (args.length < 2) return false;
                    if (!CourseManager.getInstance().contains(args[1])) return false;
                    if (sender instanceof Player player) {
                        Course course = CourseManager.getInstance().get(args[1]);
                        course.addPoint(player.getLocation());
                    }
                }

                case "run" -> {
                    if (args.length < 2) return false;
                    if (!CourseManager.getInstance().contains(args[1])) return false;

                    Course course = CourseManager.getInstance().get(args[1]);
                    RunnerManager.getInstance().add(new Runner(
                            course.getId(),
                            "Runner",
                            100
                    ));
                }

                case "save" -> CourseManager.getInstance().save();
                case "reload" -> CourseManager.getInstance().reload();
            }
        }
        return false;
    }
}
