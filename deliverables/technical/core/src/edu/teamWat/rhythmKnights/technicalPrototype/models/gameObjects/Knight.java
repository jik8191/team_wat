package edu.teamWat.rhythmKnights.technicalPrototype.models.gameObjects;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;

import edu.teamWat.rhythmKnights.technicalPrototype.utils.*;
import edu.teamWat.rhythmKnights.technicalPrototype.views.GameCanvas;

/**
 * Knight class!
 * Fill in description here!
 * */
public class Knight extends GameObject {

    private boolean isAlive = false;
    private boolean isActive = false;

    private boolean movingLeft;
    private boolean movingRight;
    private boolean movingUp;
    private boolean movingDown;

    public Vector2 position;
    public int moveCooldown;

    public static final String KNIGHT_FILE = "images/knight.png";
    public static Texture knightTexture;

    public Knight(Vector2 position){
        this.position = position;
        this.moveCooldown = 0;
        isAlive = true;
        isActive = true;
    }

    /** Do not do anything */
    public static final int CONTROL_NO_ACTION  = 0x00;
    /** Move the knight to the left */
    public static final int CONTROL_MOVE_LEFT  = 0x01;
    /** Move the knight to the right */
    public static final int CONTROL_MOVE_RIGHT = 0x02;
    /** Move the knight to the up */
    public static final int CONTROL_MOVE_UP    = 0x04;
    /** Move the knight to the down */
    public static final int CONTROL_MOVE_DOWN  = 0x08;
    /** If the player wants to jump */
    public static final int CONTROL_JUMP = 0x10;
    /** If the player wants to reset the game */
    public static final int CONTROL_RESET  = 0x40;
    /** If the player wants to exit the game */
    public static final int CONTROL_EXIT = 0x80;
    /** Move cooldown time for the knight in frames */
    public static final int MOVE_COOLDOWN = 15;


    public void update() {
        // If we are dead do nothing.
        if (!isAlive) {
            return;
        }
        //TODO: implement this

    }




    public void draw(GameCanvas canvas) {
        FilmStrip sprite = new FilmStrip(knightTexture, 1, 1);
        Vector2 loc = canvas.boardToScreen(position.x, position.y);
        canvas.draw(sprite, loc.x, loc.y);
    }


    //	/**
//	 * Moves the knight based on the input code*/
//	public void move(int code){
//
//	}
    /**
     * Preloads the assets for the Knight.
     *
     * The asset manager for LibGDX is asynchronous.  That means that
     * you tell it what to load and then wait while it
     * loads them.  This is the first step: telling it what to load.
     *
     * @param manager Reference to global asset manager.
     */
    public static void PreLoadContent(AssetManager manager) {
        manager.load(KNIGHT_FILE, Texture.class);
    }

    /**
     * Loads the assets for the Knight.
     *
     * All shell objects use one of two textures, so this is a static method.
     * This keeps us from loading the same images
     * multiple times for more than one Shell object.
     *
     * The asset manager for LibGDX is asynchronous.  That means that you tell it what to load and then wait while it
     * loads them.  This is the second step: extracting assets from the manager after it has finished loading them.
     *
     * @param manager Reference to global asset manager.
     */
    public static void LoadContent(AssetManager manager) {
        if (manager.isLoaded(KNIGHT_FILE)) {
            knightTexture = manager.get(KNIGHT_FILE,Texture.class);
            knightTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            knightTexture = null;  // Failed to load
        }
    }

    /**
     * Unloads the assets for the Knight
     *
     * This method erases the static variables.  It also deletes the associated textures from the assert manager.
     *
     * @param manager Reference to global asset manager.
     */
    public static void UnloadContent(AssetManager manager) {
        if (knightTexture != null) {
            knightTexture = null;
            manager.unload(KNIGHT_FILE);
        }
    }
}
