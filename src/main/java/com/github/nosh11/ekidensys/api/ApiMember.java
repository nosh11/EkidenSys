package com.github.nosh11.ekidensys.api;

public class ApiMember {
    public final int id;
    public final String name;
    public final int team_id;

    public int stamina;

    public ApiMember(int id, String name, int stamina, int team_id) {
        this.id = id;
        this.name = name;
        this.stamina = stamina;
        this.team_id = team_id;
    }

    public String getHeart() {
        return "♥".repeat(this.stamina);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stamina=" + stamina +
                ", teamId=" + team_id +
                '}';
    }
}