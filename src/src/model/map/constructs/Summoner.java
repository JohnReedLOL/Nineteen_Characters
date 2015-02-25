/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.model.map.constructs;

/**
 * Summoner Occupation, intellect +1.
 */
public final class Summoner implements Occupation
{
    public void change_stats(EntityStatsPack current_stats) {
        //for sneak
        current_stats.increaseIntellectLevelByOne();

    }
    
    @Override
    public String toString() {
    	return "Summoner";
    }
}
