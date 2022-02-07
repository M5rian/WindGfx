package com.github.m5rian.windGfxBot.database;

public class DbMember {
    private final long id;
    private final int xp;
    private final int level;

    public DbMember(long id, int xp, int level) {
        this.id = id;
        this.xp = xp;
        this.level = level;
    }

    public long getId() {
        return this.id;
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
    }
}
