/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do
 * anything until loading is complete. You know those loading screens with the inane tips
 * that want to be helpful?  That is asynchronous loading.
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 *
 * This code has been copied from the above original authors and modified by Gagik Hakobyan.
 */
package editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import editor.utils.*;
import editor.views.GameCanvas;

public class LoadingMode implements Screen, InputProcessor {
	// Textures necessary to support the loading screen
	// TODO: make sure these paths are correct
	// We could just use the ones from the labs
	private static final String BACKGROUND_FILE = "images/loading.png";
	private static final String PROGRESS_FILE = "images/progressbar.png";
	private static final String PLAY_BTN_FILE = "images/play.png";


	/** Background texture for start-up */
	private Texture background;
	/** Play button to display when done */
	private Texture playButton;
	/** Texture atlas to support a progress bar */
	private Texture statusBar;

	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */
	private TextureRegion statusBkgLeft;
	/** Middle portion of the status background (grey region) */
	private TextureRegion statusBkgMiddle;
	/** Right cap to the status background (grey region) */
	private TextureRegion statusBkgRight;
	/** Left cap to the status forground (colored region) */
	private TextureRegion statusFrgLeft;
	/** Middle portion of the status forground (colored region) */
	private TextureRegion statusFrgMiddle;
	/** Right cap to the status forground (colored region) */
	private TextureRegion statusFrgRight;

	/** Default budget for asset loader (do nothing but load 60 fps) */
	private static int DEFAULT_BUDGET = 15;
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	/** Ratio of the bar width to the screen */
	private static float BAR_WIDTH_RATIO = 0.66f;
	/** Ration of the bar height to the screen */
	private static float BAR_HEIGHT_RATIO = 0.25f;
	/** Height of the progress bar */
	private static int PROGRESS_HEIGHT = 30;
	/** Width of the rounded cap on left or right */
	private static int PROGRESS_CAP = 15;
	/** Width of the middle portion in texture atlas */
	private static int PROGRESS_MIDDLE = 200;
	private static float BUTTON_SCALE = 0.75f;

	/** Start button for XBox controller on Windows */
	private static int WINDOWS_START = 7;
	/** Start button for XBox controller on Mac OS X */
	private static int MAC_OS_X_START = 4;

	/** AssetManager to be loading in the background */
	private AssetManager manager;
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The width of the progress bar */
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;
	/** The height of the canvas window (necessary since sprite origin != screen origin) */
	private int heightY;
	/** Scaling factor for when the student changes the resolution. */
	private float scale;

	/** Current progress (0 to 1) of the asset manager */
	private float progress;
	/** The current state of the play button */
	private int pressState;
	/** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
	private int budget;
	/** Support for the X-Box start button in place of play button */
	private int startButton;
	/** Whether or not this player mode is still active */
	private boolean active;

	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation frame.  This allows you to do
	 * something other than load assets.  An animation frame is ~16 milliseconds. So if the budget is 10, you have 6
	 * milliseconds to do something else.  This is how game companies animate their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation frame.  This allows you to do
	 * something other than load assets.  An animation frame is ~16 milliseconds. So if the budget is 10, you have 6
	 * milliseconds to do something else.  This is how game companies animate their loading screens.
	 *
	 * @param millis the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}

	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean isReady() {
		return pressState == 2;
	}

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param manager The AssetManager to load in the background
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager) {
		this(canvas, manager, DEFAULT_BUDGET);
	}

	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation frame.  This allows you to do
	 * something other than load assets.  An animation frame is ~16 milliseconds. So if the budget is 10, you have 6
	 * milliseconds to do something else.  This is how game companies animate their loading screens.
	 *
	 * @param manager The AssetManager to load in the background
	 * @param millis  The loading budget in milliseconds
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager, int millis) {
		this.manager = manager;
		this.canvas = canvas;
		budget = millis;

		// Compute the dimensions from the canvas
		resize(canvas.getWidth(), canvas.getHeight());

		// Load the next two images immediately.
		playButton = null;
		background = new Texture(BACKGROUND_FILE);
		statusBar = new Texture(PROGRESS_FILE);

		// No progress so far.
		progress = 0;
		pressState = 0;
		active = false;

		// Break up the status bar texture into regions
		statusBkgLeft = new TextureRegion(statusBar, 0, 0, PROGRESS_CAP, PROGRESS_HEIGHT);
		statusBkgRight = new TextureRegion(statusBar, statusBar.getWidth() - PROGRESS_CAP, 0, PROGRESS_CAP, PROGRESS_HEIGHT);
		statusBkgMiddle = new TextureRegion(statusBar, PROGRESS_CAP, 0, PROGRESS_MIDDLE, PROGRESS_HEIGHT);

		int offset = statusBar.getHeight() - PROGRESS_HEIGHT;
		statusFrgLeft = new TextureRegion(statusBar, 0, offset, PROGRESS_CAP, PROGRESS_HEIGHT);
		statusFrgRight = new TextureRegion(statusBar, statusBar.getWidth() - PROGRESS_CAP, offset, PROGRESS_CAP, PROGRESS_HEIGHT);
		statusFrgMiddle = new TextureRegion(statusBar, PROGRESS_CAP, offset, PROGRESS_MIDDLE, PROGRESS_HEIGHT);

		startButton = (System.getProperty("os.name").equals("Mac OS X") ? MAC_OS_X_START : WINDOWS_START);
		Gdx.input.setInputProcessor(this);
		active = true;
	}

	/** Called when this screen should release all resources. */
	public void dispose() {
		statusBkgLeft = null;
		statusBkgRight = null;
		statusBkgMiddle = null;

		statusFrgLeft = null;
		statusFrgRight = null;
		statusFrgMiddle = null;

		background.dispose();
		statusBar.dispose();
		background = null;
		statusBar = null;
		if (playButton != null) {
			playButton.dispose();
			playButton = null;
		}
	}

	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead of using the single render()
	 * method that LibGDX does.  We will talk about why we prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
		if (playButton == null) {
			manager.update(budget);
			this.progress = manager.getProgress();
			if (progress >= 1.0f) {
				this.progress = 1.0f;
				playButton = new Texture(PLAY_BTN_FILE);
			}
		}
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead of using the single render()
	 * method that LibGDX does.  We will talk about why we prefer this in lecture.
	 */
	private void draw() {
		canvas.begin();
		canvas.draw(background, 0, 0);
		if (playButton == null) {
			drawProgress(canvas);
		} else {
			Color tint = (pressState == 1 ? Color.GRAY : Color.WHITE);
			canvas.draw(playButton, tint, playButton.getWidth() / 2, playButton.getHeight() / 2,
					centerX, centerY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
		}
		canvas.end();
	}

	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, and a rectangle in a middle.  We adjust the
	 * size of the rectangle in the middle to represent the amount of progress.
	 *
	 * @param canvas The drawing context
	 */
	private void drawProgress(GameCanvas canvas) {
		canvas.draw(statusBkgLeft, centerX - width / 2, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
		canvas.draw(statusBkgRight, centerX + width / 2 - scale * PROGRESS_CAP, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
		canvas.draw(statusBkgMiddle, centerX - width / 2 + scale * PROGRESS_CAP, centerY, width - 2 * scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);

		canvas.draw(statusFrgLeft, centerX - width / 2, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
		if (progress > 0) {
			float span = progress * (width - 2 * scale * PROGRESS_CAP) / 2.0f;
			canvas.draw(statusFrgRight, centerX - width / 2 + scale * PROGRESS_CAP + span, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
			canvas.draw(statusFrgMiddle, centerX - width / 2 + scale * PROGRESS_CAP, centerY, span, scale * PROGRESS_HEIGHT);
		} else {
			canvas.draw(statusFrgRight, centerX - width / 2 + scale * PROGRESS_CAP, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
		}
	}

	// ADDITIONAL SCREEN METHODS

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important that we only quit AFTER a
	 * draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			update(delta);
			draw();

			// We are are ready, notify our listener
			if (isReady() && listener != null) {
				//TODO: remove rhythm controller and launch exit screen
				listener.exitScreen(this, 0);
			}
		}
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// Compute the drawing scale
		float sx = ((float)width) / STANDARD_WIDTH;
		float sy = ((float)height) / STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);

		this.width = (int)(BAR_WIDTH_RATIO * width);
		centerY = (int)(BAR_HEIGHT_RATIO * height);
		centerX = width / 2;
		heightY = height;
	}

	/**
	 * Called when the Screen is paused.
	 *
	 * This is usually when it's not active or visible on screen. An Application is also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	// PROCESSING PLAYER INPUT

	/**
	 * Called when the screen was touched or a mouse button was pressed.
	 *
	 * This method checks to see if the play button is available and if the click is in the bounds of the play button.
	 * If so, it signals the that the button has been pressed and is currently down. Any mouse button is accepted.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (playButton == null || pressState == 2) {
			return true;
		}

		// Flip to match graphics coordinates
		screenY = heightY - screenY;

		// TODO: Fix scaling
		// Play button is a circle.
		float radius = BUTTON_SCALE * scale * playButton.getWidth() / 2.0f;
		float dist = (screenX - centerX) * (screenX - centerX) + (screenY - centerY) * (screenY - centerY);
		if (dist < radius * radius) {
			pressState = 1;
		}
		return false;
	}

	/**
	 * Called when a finger was lifted or a mouse button was released.
	 *
	 * This method checks to see if the play button is currently pressed down. If so, it signals the that the player is
	 * ready to go.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pressState == 1) {
			pressState = 2;
			return false;
		}
		return true;
	}

	/**
	 * Called when a key is pressed (UNSUPPORTED)
	 *
	 * @param keycode the key pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyDown(int keycode) {
		return true;
	}

	/**
	 * Called when a key is typed (UNSUPPORTED)
	 *
	 * @param character the key typed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyTyped(char character) {
		return true;
	}

	/**
	 * Called when a key is released (UNSUPPORTED)
	 *
	 * @param keycode the key released
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyUp(int keycode) {
		return true;
	}

	/**
	 * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @return whether to hand the event to other listeners.
	 */
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	/**
	 * Called when the mouse wheel was scrolled. (UNSUPPORTED)
	 *
	 * @param amount the amount of scroll from the wheel
	 * @return whether to hand the event to other listeners.
	 */
	public boolean scrolled(int amount) {
		return true;
	}

	/**
	 * Called when the mouse or finger was dragged. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}
}