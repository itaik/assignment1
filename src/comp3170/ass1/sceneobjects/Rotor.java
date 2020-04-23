
/*
 * Draws a rotor over the helicopter and handles the logic of it spinning
 */

package comp3170.ass1.sceneobjects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import comp3170.Shader;

public class Rotor extends SceneObject {
	
	private int vertexBuffer;
	
	private int clockwise; // 1 if clockwise, -1 if anti-clockwise
	
	private final float maxRotationSpeed = 7f;
	
	private float rotationSpeed = 0;
	
	private final float rotationAcceleration = 0.0008f;
	
	private boolean rotating = false;
	
	// Assuming 0, 0 is the center of the rotor
	private float[] bladeVerteces = new float[] {
			 0f,    0f, 
			-0.05f,  1f,
			 0.05f,  1f,
			 
			 0f,    0f,
			-1f,    0.05f,  
			-1f,   -0.05f,
			
			 0f,    0f,
			-0.05f, -1f,
			 0.05f, -1f,
			 
			 0f,    0f,
			 1f,    0.05f,
			 1f,   -0.05f
	};
	
	private float[] colour = {0.3f, 0.3f, 0.3f}; // Dark gray
	
	public int rotation = 0;
	
	public Rotor(Shader shader, boolean clockwise) {	
		
		this.clockwise = clockwise ? -1 : 1;;
		
	    this.vertexBuffer = shader.createBuffer(this.bladeVerteces);
	    
		this.localMatrix.scale(1.3f, 1.3f, 0);
	}
	
	public Rotor(Shader shader, boolean clockwise, SceneObject root) {
		this(shader, clockwise);
		this.setParent(root);
	}

	public Rotor(Shader shader, boolean clockwise, SceneObject root, float position) {
		this(shader, clockwise, root);
		this.localMatrix.translate(position, 0, 0);
	}

	@Override
	protected void drawSelf(Shader shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		shader.setAttribute("a_position", this.vertexBuffer);	    
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.bladeVerteces.length / 2);
	}
	
	/*
	 * Handles the logic for the acceleration of the spin of the rotors taking landing and taking off into consideration
	 */
	public void rotate(float deltaTime) {
		
		if (rotating && rotationSpeed < maxRotationSpeed) {
			rotationSpeed += rotationAcceleration;
			if (rotationSpeed > maxRotationSpeed) {
				rotationSpeed = maxRotationSpeed;
			}
		}
		else if (!rotating && rotationSpeed > 0) {
			rotationSpeed -= rotationAcceleration;
			if (rotationSpeed < 0) {
				rotationSpeed = 0;
			}
 		}
		
		this.localMatrix.rotateZ(rotationSpeed * deltaTime * clockwise);
	}
	
	public void start() {
		rotating = true;
	}

	public void stop() {
		rotating = false;
	}
	
	/*
	 * Used to decide weather the rotos are spinning fast enough to cause enough force for the helicopter to take off
	 */
	public boolean enoughForceToTakeOff() {
		return rotationSpeed >= maxRotationSpeed/2;
	}
}
