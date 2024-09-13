package com.github.nosh11.ekidensys.api;

import java.util.List;

public class ApiSession {
    public int memberId;
    public int point;
    public int time;
    public List<ApiLevel> levels;
    public int prizeCount;

    public ApiSession(int memberId, int point, int time, List<ApiLevel> levels, int prizeCount) {
        this.memberId = memberId;
        this.point = point;
        this.time = time;
        this.levels = levels;
        this.prizeCount = prizeCount;
    }

    @Override
    public String toString() {
        return "Session{" +
                "memberId=" + memberId +
                ", point=" + point +
                ", time=" + time +
                ", levels=" + levels +
                ", prizeCount=" + prizeCount +
                '}';
    }
}
