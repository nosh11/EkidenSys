package com.github.nosh11.ekidensys.util;

import org.bukkit.Location;

public class Calc {
    public static double getDistance(Location loc1, Location loc2) {
        double dx = (loc1.getX() - loc2.getX());
        double dy = (loc1.getY() - loc2.getY());
        double dz = (loc1.getZ() - loc2.getZ());
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
    }
}
