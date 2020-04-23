
/*
 * Itai Ktalav
 * 4368968190
 * Draws a blue river on the canvas
 */

package comp3170.ass1.sceneobjects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import comp3170.Shader;

public class River extends SceneObject {
	
	private int vertexBuffer;
	
	// Horrizontal blue line. 
	// Will be rotated, positioned and scaled in constructor
	private float[] vertices = new float[] {
			-1.0f,	0.0f,
			 2.0f,	0.0f,
			 2.0f,	2.0f,

			 2.0f,	2.0f,
			 -1.0f,  2.0f,
			 -1.0f,  0.0f
	};
	
	private float[] colour = {0f, 0f, 1f}; // Blue
	
	public int rotation = 0;
	
	public River(Shader shader) {		
	    this.vertexBuffer = shader.createBuffer(this.vertices);
	    
		this.localMatrix.rotateZ((float) Math.PI / 5.5f);
		this.localMatrix.scale(1f, 0.1f, 1f);
		this.localMatrix.translate(-0.3f, 0, 0);
	}
	
	public River(Shader shader, SceneObject root) {
		this(shader);
		this.setParent(root);
	}

	@Override
	protected void drawSelf(Shader shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		shader.setAttribute("a_position", this.vertexBuffer);	    
		shader.setUniform("u_colour", this.colour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.vertices.length / 2);
	}
	
	
}
