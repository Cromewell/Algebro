package com.cromewell.algebro;

import java.util.ArrayList;

public class Player {

    private String name = "emptiness o.o";
    private int level = 1;
    private int xp = 0;
    private int xpNeededForLvlUp = 10;
    private ArrayList<String> problemsSolved = new ArrayList<>();


    public void receiveXP(int xp) {
        this.xp += xp;
        if (xpNeededForLvlUp <= this.xp) {
            level += 1;
            this.xp -= xpNeededForLvlUp;
            xpNeededForLvlUp = (level ^ 2) * 10;
        }
    }

    public ArrayList<String> getProblemsSolved() {
        return problemsSolved;
    }

    public void addSolvedProblem(String problem) {
        problemsSolved.add(problem);
    }

    public int getXp() {
        return xp;
    }

    public int getXpNeededForLvlUp() {
        return xpNeededForLvlUp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
