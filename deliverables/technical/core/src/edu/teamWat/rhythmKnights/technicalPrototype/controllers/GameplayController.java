package edu.teamWat.rhythmKnights.technicalPrototype.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import edu.teamWat.rhythmKnights.technicalPrototype.models.*;
import edu.teamWat.rhythmKnights.technicalPrototype.models.gameObjects.*;
import edu.teamWat.rhythmKnights.technicalPrototype.models.Ticker.*;
import jdk.internal.util.xml.impl.Input;

import javax.swing.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Vector;

public class GameplayController {
	/** Reference to the game board */
	public Board board;
	/** Reference to all the game objects in the game */	
	public GameObjectList gameObjects;
	/** List of all the input (both player and AI) controllers */
	protected InputController[] controls;
	/** Ticker */
	public Ticker ticker;
	
	private Knight knight;

	public PlayerController playerController;

	public CollisionController collisionController;

	private ArrayList<Integer> playerActionQueue = new ArrayList<Integer>();

	boolean playerMoved = false;

	boolean gameStateAdvanced;

	boolean calibrationBeatSent;

	private boolean gameOver = false;

	public GameplayController() {	}

	public void initialize() {

        board = new Board(13, 7);
		gameObjects = new GameObjectList(9);

        controls = new InputController[gameObjects.size()];

        gameObjects.add(new Knight(0, 0, 3));
        controls[0] = playerController;

        gameObjects.add(new DynamicTile(1, 3, 3));
        int[] path = {InputController.CONTROL_MOVE_UP, InputController.CONTROL_MOVE_UP, InputController.CONTROL_MOVE_RIGHT,
                            InputController.CONTROL_MOVE_DOWN, InputController.CONTROL_MOVE_DOWN, InputController.CONTROL_MOVE_DOWN,
                            InputController.CONTROL_MOVE_DOWN, InputController.CONTROL_MOVE_LEFT, InputController.CONTROL_MOVE_UP,
                            InputController.CONTROL_MOVE_UP};
        controls[1] = new AIController(1, gameObjects, path);

        gameObjects.add(new Skeleton(2, 5, 0));
        path = new int[]{InputController.CONTROL_MOVE_LEFT, InputController.CONTROL_MOVE_RIGHT,
                InputController.CONTROL_MOVE_RIGHT, InputController.CONTROL_MOVE_LEFT};
        controls[2] = new AIController(2, gameObjects, path);

		gameObjects.add(new Skeleton(3, 5, 6));
        path = new int[]{InputController.CONTROL_MOVE_LEFT, InputController.CONTROL_MOVE_RIGHT,
                InputController.CONTROL_MOVE_RIGHT, InputController.CONTROL_MOVE_LEFT};
        controls[3] = new AIController(3, gameObjects, path);

		gameObjects.add(new Slime(4, 7, 0));
        path = new int[]{InputController.CONTROL_MOVE_UP, InputController.CONTROL_MOVE_RIGHT,
                InputController.CONTROL_MOVE_DOWN, InputController.CONTROL_MOVE_LEFT};
        controls[4] = new AIController(4, gameObjects, path);

        gameObjects.add(new Slime(5, 7, 6));
        path = new int[]{InputController.CONTROL_MOVE_LEFT, InputController.CONTROL_MOVE_UP,
                InputController.CONTROL_MOVE_RIGHT, InputController.CONTROL_MOVE_DOWN};
        controls[5] = new AIController(5, gameObjects, path);

        gameObjects.add(new Slime(6, 9, 2));
        path = new int[]{InputController.CONTROL_MOVE_RIGHT, InputController.CONTROL_MOVE_DOWN,
                InputController.CONTROL_MOVE_LEFT, InputController.CONTROL_MOVE_UP};
        controls[6] = new AIController(6, gameObjects, path);

        gameObjects.add(new Slime(7, 9, 4));
        path = new int[]{InputController.CONTROL_MOVE_RIGHT, InputController.CONTROL_MOVE_UP,
                InputController.CONTROL_MOVE_LEFT, InputController.CONTROL_MOVE_DOWN};
        controls[7] = new AIController(7, gameObjects, path);

        gameObjects.add(new Slime(8, 11, 3));
        path = new int[]{InputController.CONTROL_MOVE_UP, InputController.CONTROL_MOVE_DOWN,
                InputController.CONTROL_MOVE_DOWN, InputController.CONTROL_MOVE_UP};
        controls[8] = new AIController(8, gameObjects, path);

		board.setTile(0, 3, false, true, false);
		board.setTile(3, 1, false, false, true);
		board.setTile(3, 2, false, false, true);
		board.setTile(3, 3, false, false, true);
		board.setTile(3, 4, false, false, true);
		board.setTile(3, 5, false, false, true);
		board.setTile(4, 1, false, false, true);
		board.setTile(4, 2, false, false, true);
		board.setTile(4, 3, false, false, true);
		board.setTile(4, 4, false, false, true);
		board.setTile(4, 5, false, false, true);
		board.setTile(7, 3, false, false, true);
		board.setTile(8, 3, false, false, true);
		board.setTile(9, 3, false, false, true);
		board.setTile(10, 3, false, false, true);
		board.setTile(12, 2, false, false, true);
		board.setTile(12, 3, true, false, false);
		board.setTile(12, 4, false, false, true);

		ticker = new Ticker(new TickerAction[] {TickerAction.MOVE, TickerAction.MOVE, TickerAction.MOVE, TickerAction.DASH});

		collisionController = new CollisionController(board, gameObjects);

		knight = (Knight)gameObjects.getPlayer();
		knight.setInvulnerable(true);
		playerMoved = true;
		calibrationBeatSent = true;
		gameStateAdvanced = true;
	}


	public void update() {

		if (RhythmController.updateBeat()) {
			//Final actions
			if (!playerMoved) {
				damagePlayer();
				advanceGameState();
			}

			// Cleanup
			gameStateAdvanced = false;
			playerMoved = false;
			calibrationBeatSent = false;
		} else {
			if (playerMoved) {
				if (playerController.numKeyEvents > 0){
					damagePlayer();
				}
			} else {
				switch (ticker.getAction()) {
					case MOVE:
						if (playerController.numKeyEvents > 1) {
							damagePlayer();
							advanceGameState();
							playerMoved = true;
						} else if (playerController.numKeyEvents == 1) {
							PlayerController.KeyEvent event = playerController.keyEvents[0];

							//DEBUGGING CODE
							RhythmController.isWithinActionWindow(event.time, 0, true);

							// Send a calibration beat
							if (!calibrationBeatSent) {
								RhythmController.sendCalibrationBeat(event.time);
								calibrationBeatSent = true;
							}



							if (RhythmController.isWithinActionWindow(event.time, 0, false)) {
								Vector2 vel = new Vector2();
								switch (event.code) {
									case InputController.CONTROL_MOVE_RIGHT:
										vel.x = 1;
										break;
									case InputController.CONTROL_MOVE_UP:
										vel.y = 1;
										break;
									case InputController.CONTROL_MOVE_LEFT:
										vel.x = -1;
										break;
									case InputController.CONTROL_MOVE_DOWN:
										vel.y = -1;
										break;
								}
								playerMoved = true;
								knight.setVelocity(vel);
								advanceGameState();
							}
						}
						break;
					case DASH:
						if (playerController.numKeyEvents > 1) {
							damagePlayer();
							advanceGameState();
							playerMoved = true;
						} else if (playerController.numKeyEvents == 1) {
							PlayerController.KeyEvent event = playerController.keyEvents[0];

							//DEBUGGING CODE
							RhythmController.isWithinActionWindow(event.time, 0, true);

							// Send a calibration beat
							if (!calibrationBeatSent) {
								RhythmController.sendCalibrationBeat(event.time);
								calibrationBeatSent = true;
							}


							if (RhythmController.isWithinActionWindow(event.time, 0, false)) {
								Vector2 vel = new Vector2();
								switch (event.code) {
									case InputController.CONTROL_MOVE_RIGHT:
										vel.x = 1;
										break;
									case InputController.CONTROL_MOVE_UP:
										vel.y = 1;
										break;
									case InputController.CONTROL_MOVE_LEFT:
										vel.x = -1;
										break;
									case InputController.CONTROL_MOVE_DOWN:
										vel.y = -1;
										break;
								}
								playerMoved = true;
								knight.setVelocity(vel);
								advanceGameState();
							}
						}
						break;
				}
			}
		}

		playerController.clear();



//
//		if (RhythmController.updateBeat()) {
//			//Final actions
//			if (!playerMoved) {
//				damagePlayer();
//				advanceGameState();
//			}
//
//			// Cleanup
//			gameStateAdvanced = false;
//			playerMoved = false;
//			calibrationBeatSent = false;
//		} else {
//			if (code != InputController.CONTROL_NO_ACTION) {
//
//				//DEBUGGING CODE
//				RhythmController.isWithinActionWindow(playerController.getLastAction().time, 0, true);
//
//				// Send a calibration beat
//				if (!calibrationBeatSent) {
//					if (ticker.getAction() == TickerAction.MOVE) {
//						RhythmController.sendCalibrationBeat(playerController.getLastAction().time);
//						calibrationBeatSent = true;
//					}
//				}
//
//				if (playerMoved) {
//					damagePlayer();
//				} else {
//					if (RhythmController.isWithinActionWindow(playerController.getLastAction().time, 0, false)) {
//						switch (ticker.getAction()) {
//							case MOVE:
//								Vector2 moveDirection = new Vector2(0, 0);
//								boolean wasInv = knight.isInvulnerable();
//								if (code == InputController.CONTROL_MOVE_RIGHT) {
//									moveDirection.x = 1;
//									knight.setInvulnerable(false);
//								} else if (code == InputController.CONTROL_MOVE_UP) {
//									moveDirection.y = 1;
//									knight.setInvulnerable(false);
//								} else if (code == InputController.CONTROL_MOVE_LEFT) {
//									moveDirection.x = -1;
//									knight.setInvulnerable(false);
//								} else if (code == InputController.CONTROL_MOVE_DOWN) {
//									moveDirection.y = -1;
//									knight.setInvulnerable(false);
//								} else {
//									damagePlayer();
//								}
//
//								knight.move(moveDirection);
//								if (knight.getPosition().x < 0 || knight.getPosition().x >= board.getWidth()
//										|| knight.getPosition().y < 0 || knight.getPosition().y >= board.getHeight()){
//									knight.move(moveDirection.scl(-1));
//									if (wasInv) knight.setInvulnerable(true);
//								}
//								playerMoved = true;
//								advanceGameState();
//								break;
//							case DASH:
////								// debugging
////								moveDirection = new Vector2(0, 0);
////								if (code == InputController.CONTROL_MOVE_RIGHT) {
////									moveDirection.x = 1;
////									knight.setInvulnerable(false);
////								} else if (code == InputController.CONTROL_MOVE_UP) {
////									moveDirection.y = 1;
////									knight.setInvulnerable(false);
////								} else if (code == InputController.CONTROL_MOVE_LEFT) {
////									moveDirection.x = -1;
////									knight.setInvulnerable(false);
////								} else if (code == InputController.CONTROL_MOVE_DOWN) {
////									moveDirection.y = -1;
////									knight.setInvulnerable(false);
////								} else {
////									damagePlayer();
////								}
////
////								knight.move(moveDirection);
////								if (knight.getPosition().x < 0 || knight.getPosition().x >= board.getWidth()
////										|| knight.getPosition().y < 0 || knight.getPosition().y >= board.getHeight()) {
////									knight.move(moveDirection.scl(-1));
////								}
//
//
//								playerMoved = true;
//								advanceGameState();
//								break;
//						}
//					}
//				}
//			}
//		}
	}

	private void advanceGameState () {
		if (!gameStateAdvanced) {
			ticker.advance();
			board.updateColors();
			collisionController.update();
			gameStateAdvanced = true;

			// Configures the next beat to handle inputs properly
//			switch (ticker.getAction()) {
//				case MOVE:
//					RhythmController.actionWindowRadius = 0.15f;
//					RhythmController.finalActionOffset = 0.5f;
//
//					break;
//				case DASH:
//
//
//					break;
//			}


		}
	}

	public void damagePlayer() {
		if (!knight.isInvulnerable()) {
			knight.takeDamage();
			knight.setInvulnerable(true);
		}
	}

	public boolean isGameOver() {
		return gameOver;
	}
}
