package com.github.nosh11.ekidensys.runner;

import net.citizensnpcs.api.npc.NPC;

import java.util.ArrayList;
import java.util.List;

public class RunnerManager {
    private static final RunnerManager instance = new RunnerManager();
    public static RunnerManager getInstance() {
        return instance;
    }

    private final List<Runner> runner_list = new ArrayList<>();

    public void add(Runner runner) {
        runner_list.add(runner);
    }

    public void add(NPC npc) {

    }

    public Runner get(int i) {
        return runner_list.get(i);
    }

    public List<Runner> getAll() {
        return runner_list;
    }

}
