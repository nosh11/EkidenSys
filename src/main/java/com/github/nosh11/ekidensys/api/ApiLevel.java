package com.github.nosh11.ekidensys.api;

public class ApiLevel {
    int time;
    int death;
    int prize;

    public ApiLevel(int time, int death, int prize) {
        this.time = time;
        this.death = death;
        this.prize = prize;
    }

    @Override
    public String toString() {
        return "Level{" +
                "time=" + time +
                ", death=" + death +
                ", prize=" + prize +
                '}';
    }
}
