package edu.teamWat.rhythmKnights.alpha.music;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

public class GameTrackRunnable implements Runnable {

    private GameTrack1 gametrack;
    private Player player;

    public GameTrackRunnable() {
    	this.gametrack = new GameTrack1();
    	this.player = new Player();
    }

    public void run() {
		Pattern pattern = gametrack.getPattern();
		Player player = new Player();
		player.play(pattern);
    }
}