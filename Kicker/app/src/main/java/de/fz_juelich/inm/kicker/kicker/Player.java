package de.fz_juelich.inm.kicker.kicker;

/**
 * Created by weidel on 06.02.15.
 */
public class Player implements Comparable<Player>{

    int id;
    String name;
    int score;

    Player(int id, String name, int score){
        this.id = id;
        this.name = name;
        this.score = score;
    }

    @Override
    public int compareTo(Player other){
        return this.name.compareTo(other.name);
    }

}
