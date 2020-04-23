
/*
 * Itai Ktalav
 * 4368938190
 * Drawing the helipad and changing the corners to show when the helicopter can land
 */

package comp3170.ass1.sceneobjects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import comp3170.Shader;

public class HeliPad extends SceneObject {
	
	private boolean okToLand = true; // Since we start off on the pad
	
	private int vertexBuffer;
	private int iVertexBuffer;
	private int hVertexBuffer;
	
	// The square that makes up the base
	private float[] baseVertices = new float[] {
			-1.0f, -1.0f,
			 1.0f, -1.0f,
			 1.0f,	1.0f,

			 1.0f,	1.0f,
			-1.0f,  1.0f,
			-1.0f, -1.0f
	};
	
	// The Landing indicators on the corners
	private float[] iVertices = new float[] {
			-1.0f, -1.0f,
			-0.8f, -1.0f,
			-1.0f, -0.8f,
			
			 1.0f,	1.0f,
			 0.8f,  1.0f,
			 1.0f,  0.8f,
			 
			-1.0f,  1.0f,
			-1.0f,  0.8f,
			-0.8f,  1.0f,
			
			 1.0f, -1.0f,
			 1.0f, -0.8f,
			 0.8f, -1.0f
	};
	
	//The H that sits on top
	private float[] hVertices = new float[] {
		    -0.75f, -0.75f,
		    -0.75f,  0.75f,
		    -0.25f,  0.75f,
		     
		    -0.75f, -0.75f,
		    -0.25f,  0.75f,
		    -0.25f, -0.75f,
		     
		    -0.25f, -0.25f,
		    -0.25f,  0.25f,
		     0.25f,  0.25f,
		     
		    -0.25f, -0.25f,
		     0.25f,  0.25f,
		     0.25f, -0.25f,
		     
		     0.25f, -0.75f,
		     0.25f,  0.75f,
		     0.75f,  0.75f,
		     
		     0.25f, -0.75f,
		     0.75f,  0.75f,
		     0.75f, -0.75f
	};
	
	private float[] baseColour  = {0.75f, 0.75f, 0f}; // Dark-ish yellow
	private float[] hColour     = {1f, 1f, 1f}; // White
	private float[] iColour;
	
	private float[] notOkColour = {1f, 0f, 0f}; //Red
	private float[] okColour    = {0f, 0.75f, 0f}; // Dark-ish green
	
	public HeliPad(Shader shader) {		
	    this.vertexBuffer = shader.createBuffer(this.baseVertices);
	    this.iVertexBuffer = shader.createBuffer(this.iVertices);
	    this.hVertexBuffer = shader.createBuffer(this.hVertices);
	    setOkToLand(false); // To initialise it 
	}
	
	public HeliPad(Shader shader, SceneObject root, float x, float y) {
		this(shader);
		this.setParent(root);
		this.localMatrix.scale(0.15f, 0.15f, 1);
		this.localMatrix.translate(x, y, 0);
	}

	@Override
	protected void drawSelf(Shader shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// Draw Base
		shader.setAttribute("a_position", this.vertexBuffer);	    
		shader.setUniform("u_colour", this.baseColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.baseVertices.length / 2);
        
        // Draw Indicators
		shader.setAttribute("a_position", this.iVertexBuffer);	    
		shader.setUniform("u_colour", this.iColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.iVertices.length / 2);
        
        // Draw H
        shader.setAttribute("a_position", this.hVertexBuffer);	    
		shader.setUniform("u_colour", this.hColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.hVertices.length / 2);
	}
	
	/*
	 * Changes the colour of the indicator lights on the corners of the helipads to show weather it's ok for the user to land the helicopter or not
	 */
	public void setOkToLand(boolean okToLand) {
		this.okToLand = okToLand;
		
		if (this.okToLand) {
			this.iColour = this.okColour;
		}
		else {
			this.iColour = this.notOkColour;
		}
	}
	
	public boolean getOkToland() {
		return okToLand;
	}
}
