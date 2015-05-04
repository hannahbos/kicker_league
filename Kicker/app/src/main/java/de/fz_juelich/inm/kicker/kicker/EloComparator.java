package de.fz_juelich.inm.kicker.kicker;

/**
 * Created by schmidt on 01.05.15.
 */

import java.util.Comparator;

public class EloComparator implements Comparator<Player>  {
    public int compare(Player p1, Player p2){
        if (p1.elo <= p2.elo)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
