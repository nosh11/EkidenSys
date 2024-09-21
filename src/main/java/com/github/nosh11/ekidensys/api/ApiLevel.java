package com.github.nosh11.ekidensys.api;

public class ApiLevel {
    public long time;
    public int death;
    public int prize;

    public ApiLevel(long time, int death, int prize) {
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
