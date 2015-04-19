/*
 * PlayerController.java
 * 
 * This class provides the human player an interface to move the knight
 * 
 * Author: Team Wat
 * Heavily based on 2015 CS 3152 Game Lab 2 by 
 * Walker M. White, Cristian Zaloj, which was based on the
 * original AI Game Lab by Yi Xu and Don Holden, 2007
 */
package edu.teamWat.rhythmKnights.alpha.controllers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.org.apache.bcel.internal.classfile.Code;

import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerController implements InputController, InputProcessor {

	public ArrayList<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	public boolean didReset = false;

	public static long inputOffset = 0;

	/**
	 * Return the action of this knight (but do not process)
	 *
	 * The value returned must be some bitmasked combination of the static ints
	 * in the implemented interface.
	 *
	 * @return the action of this ship
	 */
	@Deprecated
    public int getAction() {
	    return keyEvents.get(keyEvents.size()-1).code;
    }

	public void clear() {
		keyEvents.clear();
		didReset = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.A:
				addKeyEvent(CONTROL_MOVE_LEFT, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.D:
				addKeyEvent(CONTROL_MOVE_RIGHT, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.W:
				addKeyEvent(CONTROL_MOVE_UP, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.S:
				addKeyEvent(CONTROL_MOVE_DOWN, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.LEFT:
				addKeyEvent(CONTROL_MOVE_LEFT, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.RIGHT:
				addKeyEvent(CONTROL_MOVE_RIGHT, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.UP:
				addKeyEvent(CONTROL_MOVE_UP, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.DOWN:
				addKeyEvent(CONTROL_MOVE_DOWN, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.R:
				didReset = true;
				break;
		}
		System.out.println(RhythmController.getSequencePosition() - inputOffset);
		return true;
	}

	public synchronized void addKeyEvent(int code, long time) {
		keyEvents.add(new KeyEvent(code, time));
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Keys.A:
				addKeyEvent(CONTROL_MOVE_LEFT | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.D:
				addKeyEvent(CONTROL_MOVE_RIGHT | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.W:
				addKeyEvent(CONTROL_MOVE_UP | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.S:
				addKeyEvent(CONTROL_MOVE_DOWN | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.LEFT:
				addKeyEvent(CONTROL_MOVE_LEFT | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.RIGHT:
				addKeyEvent(CONTROL_MOVE_RIGHT | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.UP:
				addKeyEvent(CONTROL_MOVE_UP | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
			case Keys.DOWN:
				addKeyEvent(CONTROL_MOVE_DOWN | CONTROL_RELEASE, (RhythmController.getSequencePosition() - inputOffset));
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public class KeyEvent {
		int code = 0;
		long time = 0;

		public KeyEvent(int code, long time) {
			this.code = code;
			this.time = time;
		}
	}
}