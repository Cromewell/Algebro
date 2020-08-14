package com.cromewell.algebro;

public class Player {

    private String name = "emptiness o.o";
    private int level = 1;
    private int xp = 0;
    private int xpNeededForLvlUp = 10;
    private int problemsSolved = 0;
    private String lastProblemSolved = "Nothing yet";


    public void receiveXP(int xp) {
        this.xp += xp;
        if (xpNeededForLvlUp <= this.xp) {
            level += 1;
            this.xp -= xpNeededForLvlUp;
            xpNeededForLvlUp = (level ^ 2) * 10;
            System.out.println("Level steigt auf " + level);
        }
        problemsSolved += 1;
    }

    public int getProblemsSolved() {
        return problemsSolved;
    }

    public void setProblemsSolved(int problemsSolved) {
        this.problemsSolved = problemsSolved;
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

    public void setLastProblemSolved(String lastProblemSolved) {
        this.lastProblemSolved = lastProblemSolved;
    }

    public void setName(String name) {
        this.name = name;
    }
}
