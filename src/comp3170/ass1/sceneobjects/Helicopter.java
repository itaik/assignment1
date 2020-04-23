
/*
 * Itai Ktalav
 * 43681913
 * Handles the drawing of the helicopter to the canvas and also movement logic
 */

package comp3170.ass1.sceneobjects;

import org.joml.Vector3f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import comp3170.Shader;

public class Helicopter extends SceneObject {
	
	private int vertexBuffer;
	
	private int noseVertexBuffer;
	
	private float moveSpeed = 0;
	
	private final float maxMoveSpeed = 3f;
	
	private final float acceleration = 0.003f;
	
	private boolean moving = false;
	
	// This also server to track for when the helipcopter is landed on the pad
	private boolean landing = true;
	
	// If the destination coordinates are -1, that means it't not currently going to mouse coordinates. 
	// If they are positive numbers, the helicopter will go there.
	private float destinationX = -1;
	
	private float destinationY = -1;
	
	private float deltaTime;
	
	private final float rotationSpeed = 1.5f;
	
	private boolean landed = false;
	
	private Rotor rotor1;
	
	private Rotor rotor2;
	
	// I decided to draw it this convoluted way because it seemed like a good idea at the time
	// Only about 2/3 of the way did I realise it would have been just as easy to do it the intuitive way
	// This diagram should make the division of triangles seem clear: https://imgur.com/a/nbUPIf7
	// I inverted the x and y axis to make the rotations easier
	private float[] bodyVertices = new float[] {
			-1f,   -0.5f,
			 1f,   -0.5f,
			 1.5f,	0f,

			-1f,   -0.5f,
			 1.5f,  0f,
			 1f,    0.5f,
			 
			-1f,   -0.5f,
			 1f,    0.5f,
			-1.5f,  0f,
			 
			 1f,    0.5f,
			-1.5f,  0f,
			-1f,    0.5f
	};
	
	private float[] noseWindowVertices = new float[] {
			 1f,      0.375f,
			 1f,      0.25f,
			 1.2f,    0f,
			 
			 1f,      0.375f,
			 1.2f,    0f,
			 1.375f,  0f,
			 
			 1.2f,    0f,
			 1.375f,  0f,
			 1f,     -0.375f,
			 
			 1.2f,    0f,
			 1f,     -0.25f,
			 1f,     -0.375f,
	};
	
	private float[] colour = {0.65f, 0.65f, 0.65f}; // Light gray
	private float[] noseWindwoColour = {0.35f, 0.35f, 0.35f}; // Dark gray
	
	public Helicopter(Shader shader) {		
	    this.vertexBuffer = shader.createBuffer(this.bodyVertices);
	    this.noseVertexBuffer = shader.createBuffer(this.noseWindowVertices);
	    
	    rotor1 = new Rotor(shader, true,  this, 0.5f);
	    rotor2 = new Rotor(shader, false, this, -0.6f);

	    this.localMatrix.scale(0.08f, 0.08f, 1);
	}
	
	public Helicopter(Shader shader, SceneObject root, float startX, float startY) {
		this(shader);
		this.setParent(root);
		this.localMatrix.setTranslation(startX, startY, 0);
	}

	@Override
	protected void drawSelf(Shader shader) {
		
		move(); // Move the helicopter before we draw it
		
		GL4 gl = (GL4) GLContext.getCurrentGL();

		shader.setAttribute("a_position", this.vertexBuffer);	    
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.bodyVertices.length / 2);
        
        shader.setAttribute("a_position", this.noseVertexBuffer);
        shader.setUniform("u_colour", this.noseWindwoColour);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.noseWindowVertices.length / 2);
	}
	
	/*
	 * Moved the helicopter based on speed and acceleration
	 * The implication of this code is that when you press for the helicopter to move forward, it will slowly rise to it's max speed by its acceleration amount over time
	 * On the flip side, when the button is released it won't stop right away but will slow down to a halt
	 */
	private void move() {
		
		if (this.landing) {
			handleLanding();
		}
		else {
			handleTakingOff();
			
			if (rotor1.enoughForceToTakeOff()) { // Only allow motion if the rotors are spinning fast enough so that the helicopter isn't on the helipad anymore
				if (this.destinationX != -1 && this.destinationY != -1) { // Moving to a point
					moveByMousePoint();
				}
				else {
					moveByButtonPress();
				}
			}
		}
		
		
		// Moving it along the x in the local matrix means it will always go in the direction it's facing.
		this.localMatrix.translate(this.moveSpeed * this.deltaTime, 0, 0);
	}

	/*
	 * If taking off, scales the helicopter to make it look like it's rising
	 */
	private void handleTakingOff() {
		if (rotor1.enoughForceToTakeOff() && this.localMatrix.getScale(new Vector3f()).x < 0.15) {
			float factor = 1.0001f;
			this.localMatrix.scale(factor, factor, 1f);
		}
	}

	/*
	 * If the helicopter is landing, scales it down to make it looks like it's falling.
	 * Also tracks landing progress 
	 */
	private void handleLanding() {
		if (this.localMatrix.getScale(new Vector3f()).x > 0.08) { // Number finalised through testing
			float factor = 0.9999f;
			this.localMatrix.scale(factor, factor, 1f);
		}
		else { // When the helicopter is resting on the helipad
			landed = true;
		}
	}

	/*
	 * Sets moveSpeed and also rotates
	 */
	private void moveByMousePoint() {
		rotateToMouseCoordinates();
		moveTowardsMouseCoordinates();
	}
	
	/*
	 * Translates the helicopter so that it moves towards the 
	 * It decides weather the helicaopter is facing an angle close enough to the mouse click point before moving forwards
	 */
	private void moveTowardsMouseCoordinates() {
		if (Math.abs(diffrence()) > Math.PI/(10*distanceFromDestination())) { // If we're close to the correct direction
			if (distanceFromDestination() > 0.22) { // I came up with this number through testing. 
				accelerate();
			}
			else {
				decelerate();
			}
		}
	}

	/*
	 * Set's the rotation of the helicopter so that it slowly rotates to face the point that was clicked
	 */
	private void rotateToMouseCoordinates() {
		// Decide weather the angle is close enough. If it is, we don't need to keep turning it
		if (diffrence() < 3.141 && diffrence() > -3.141) { // The difference between the number here and Math.PI is how close the rotation needs to be to the mouse click point before we stop rotating
			//  Decide weather to turn clockwise or anti-clockwise
			if (diffrence() > 0) {
				this.localMatrix.rotateZ(-this.rotationSpeed * this.deltaTime);
				if (diffrence() <= 0 ) { // If we've rotated too far, undo the rotation
					this.localMatrix.rotateZ(this.rotationSpeed * this.deltaTime);
				}
			}
			else {
				this.localMatrix.rotateZ(this.rotationSpeed * this.deltaTime);
				if (diffrence() > 0) { // If we've rotated too far, undo the rotation
					this.localMatrix.rotateZ(-this.rotationSpeed * this.deltaTime);
				}
			}
		}
	}
	
	/*
	 * Finds the signed radian difference between the angle the helicopter is facing and the direction to its destination
	 */
	private double diffrence() {
		double angleToDestination = Math.atan2(this.getYPosition() - destinationY, this.getXPosition() - destinationX);
		return Math.atan2(Math.sin(angleToDestination - faceingAngle()), Math.cos(angleToDestination - faceingAngle())); 
	}
	
	/*
	 * Finds the distance between the helicopter and the mouse click destination
	 */
	public double distanceFromDestination() {
		return Math.sqrt(Math.pow((this.getXPosition() - this.destinationX), 2) + Math.pow(this.getYPosition() - this.destinationY, 2));
	}

	/*
	 * Sets moveSpeed
	 */
	private void moveByButtonPress() {
		if (this.moving) {
			accelerate();
		}
		else if (this.maxMoveSpeed > 0) {
			decelerate();
		}
	}
	
	private void accelerate() {
		this.moveSpeed += this.acceleration;
		if (this.moveSpeed > this.maxMoveSpeed) {
			this.moveSpeed = this.maxMoveSpeed;
		}
	}
	
	private void decelerate() {
		this.moveSpeed -= this.acceleration;
		if (this.moveSpeed < 0) {
			this.moveSpeed = 0;
		}
	}

	/*
	 * Move the helicopter forward based on the angle its angle and speed
	 */
	public void setMove(boolean moving) {
		
		if (!this.moving && moving) { // If we we'rent moving before but now we are
			resetDestination();       // This is so that the helicopter stops moving to the mouse coordinates when a move key is pressed
		}
		
		this.moving = moving;
	}
	
	/*
	 * Rotates the body of the helicopter
	 */
	public void rotate(boolean left) {
		if (!landed && rotor1.enoughForceToTakeOff()) { 
			int negation = left ? 1 : -1;
			resetDestination();
			this.localMatrix.rotateZ(rotationSpeed * negation * deltaTime);
		}
	}
	
	/*
	 * Keep both rotors spinning
	 */
	public void rotateRotors(float deltaTime) {
		this.rotor1.rotate(deltaTime);
		this.rotor2.rotate(deltaTime);
	}

	/*
	 * When this method is called, the helicopter will move to the destination coordinates
	 */
	public void setDestination(float x, float y) {
		// Convert mouse coordinates to NDC coordinates
		this.destinationX = (x / 1000) * 2 - 1;
		this.destinationY = (1 - (y / 1000)) * 2 - 1;
	}
	
	/*
	 * When this method is called, the helicopter will stop moving to the destination coordinates
	 */
	public void resetDestination() {
		this.destinationX = -1;
		this.destinationY = -1;
	}

	public boolean landing() {
		return this.landing;
	}

	public void land() {
		this.landing = true;
		rotor1.stop();
		rotor2.stop();
	}
	
	public void takeOff() {
		this.landing = false;
		this.landed = false;
		rotor1.start();
		rotor2.start();
	}

	public void setDeltaTime(float deltaTime) {
		this.deltaTime = deltaTime;
	}
	
	
}