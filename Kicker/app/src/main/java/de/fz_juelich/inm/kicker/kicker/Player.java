package de.fz_juelich.inm.kicker.kicker;

/**
 * Created by weidel on 06.02.15.
 */
public class Player implements Comparable<Player>{

    int id;
    String name;
    int score;
    int elo;

    Player(int id, String name, int score, int elo){
        this.id = id;
        this.name = name;
        this.score = score;
        this.elo = elo;
    }

    @Override
    public int compareTo(Player other){
        return this.name.compareTo(other.name);
    }

}
