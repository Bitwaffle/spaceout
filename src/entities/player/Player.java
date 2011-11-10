package entities.player;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import util.Runner;
import util.debug.DebugManager;
import util.helper.KeyboardHandler;
import util.helper.ModelHandler;
import util.helper.MouseHandler;
import util.helper.QuaternionHelper;
import util.helper.TextureHandler;
import entities.Entities;
import entities.Entity;
import entities.weapons.bullets.LaserBullet;
import graphics.model.Model;
import graphics.model.ModelLoader;

public class Player extends Entity {
	//private int model = ModelHandler.SHIP1;
	private Model model;
	private boolean button0Down = false;

	public Player() {
		super();
		type = "player";

		rotationBuffer = BufferUtils.createFloatBuffer(16);

		xSpeed = 0.0f;
		ySpeed = 0.0f;
		zSpeed = 0.0f;

		maxZSpeed = 75.0f;
		zAccel = 0.07f;
		zDecel = 0.04f;

		maxXSpeed = 75.0f;
		xAccel = 0.07f;
		xDecel = 0.04f;

		maxYSpeed = 75.0f;
		yAccel = 0.07f;
		yDecel = 0.04f;
		
		model = ModelLoader.loadObjFile("res/models/ships/ship1.obj", 0.5f);
	}

	@Override
	public void update() {
		/*
		 * this should be grabbed every frame by any entity that moves An
		 * entity's movement amount (speed) should be multiplied by this so that
		 * the movement isn't framerate dependent.
		 */
		int delta = getDelta();
		if (!Runner.paused) {
			boolean forward = KeyboardHandler.forward;
			boolean backward = KeyboardHandler.backward;

			boolean left = KeyboardHandler.left;
			boolean right = KeyboardHandler.right;

			// control forawrd and backward movement
			if (forward || backward) {
				if (forward) {
					accelerateZPos(delta);
				}
				if (backward) {
					accelerateZNeg(delta);
				}
			} else if (zSpeed != 0) {
				decelerateZ(delta);
			}

			moveZ(zSpeed);

			// control strafing left and right
			if (left || right) {
				if (left) {
					accelerateXPos(delta);
				}
				if (right) {
					accelerateXNeg(delta);
				}
			} else if (xSpeed != 0) {
				decelerateX(delta);
			}

			moveX(xSpeed);

			// handle going up/down
			boolean up = KeyboardHandler.ascend;
			boolean down = KeyboardHandler.descend;
			if (up || down) {
				if (up)
					accelerateYNeg(delta);
				if (down)
					accelerateYPos(delta);
			} else if (ySpeed != 0) {
				decelerateY(delta);
			}

			moveY(ySpeed);

			// apply any rotation changes
			if (!Entities.camera.vanityMode) {
				rotateX(MouseHandler.dy);
				rotateY(MouseHandler.dx);
			}

			// roll left/right
			boolean rollRight = KeyboardHandler.rollRight;
			boolean rollLeft = KeyboardHandler.rollLeft;
			if (rollRight)
				rotateZ(-delta / 10.0f);
			if (rollLeft)
				rotateZ(delta / 10.0f);

			// shoot bullets if the player clicked
			if (MouseHandler.button0 && !button0Down
					&& !(DebugManager.consoleOn)) {
				LaserBullet bullet = new LaserBullet(this.location.x,
						this.location.y, this.location.z, this.rotation);
				Entities.addBuffer.add(bullet);
				button0Down = true;
			}

			if (!MouseHandler.button0) {
				button0Down = false;
			}
		}
	}

	@Override
	public void draw() {
		// bind the texture FIXME does not work!
		TextureHandler.getTexture(TextureHandler.SHIP1).bind();
		// make it blue FIXME also does not work!
		GL11.glColor3f(0.0f, 1.0f, 0.0f);
		GL11.glPushMatrix();
		{
			// apply reverse rotation (counteracts the camera rotation)
			Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
			rotation.negate(revQuat);
			QuaternionHelper.toFloatBuffer(revQuat, rotationBuffer);
			GL11.glMultMatrix(rotationBuffer);

			// rotate so that the model is facing the right way
			/*
			 * For the monkey ship GL11.glRotatef(-165.0f, 1.0f, 0.0f, 0.0f);
			 * GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
			 */

			GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
			//GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);

			// draw the model
			GL11.glCallList(model.getCallList());
			//GL11.glCallList(ModelHandler.getCallList(model));
		}
		GL11.glPopMatrix();
	}
}
