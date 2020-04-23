
/*
 * Itai Ktalav
 * 43681913
 * Draws a tree with a given number of vertices for the branches/leaves part
 */

package comp3170.ass1.sceneobjects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import comp3170.Shader;

public class Tree extends SceneObject {
	
	private int vertexBuffer;
	private int leavesVertexBuffer;
	
	// The trunk of the tree
	private float[] trunkVertices = new float[] {
			-0.3f, -2.0f,
			 0.3f, -2.0f,
			 0.3f,	0.0f,

			 0.3f,	0.0f,
			-0.3f,  0.0f,
			-0.3f, -2.0f
	};
	// The leaves on top represented by a dynamically created polygon
	// This array is populated by the createLeaves() function
	private float[] leavesVertices;
	private final float leavesRadius = 1f;
	
	private float[] trunkColour     = {0.6f, 0.4f, 0f}; // Brown
	private float[] leavesColour    = {0f,   0.4f, 0f}; // Dark Green
	
	public Tree(Shader shader, int polyNum) {		
	    this.vertexBuffer = shader.createBuffer(this.trunkVertices);
	    
	    createLeaves(polyNum);
	    
	    this.leavesVertexBuffer = shader.createBuffer(this.leavesVertices);  
	    
	    
	}

	public Tree(Shader shader, int polyNum, SceneObject root, float x, float y) {
		this(shader, polyNum);
		this.setParent(root);
		this.localMatrix.scale(0.1f, 0.1f, 1);
		this.localMatrix.translate(x, y, 0);
	}
	
	/*
	 * Populates the leavesVertices array with points for a polygon with polyNum number of sides 
	 */
	private void createLeaves(int polyNum) {
		leavesVertices = new float[polyNum*3*2]; // Because each triangle is 3 points of 2 dimensions
		for (int i = 0; i < polyNum; i++) {
			leavesVertices[i*6]   = (float) (leavesRadius * Math.cos(2 * Math.PI * i / polyNum));    
			leavesVertices[i*6+1] = (float) (leavesRadius * Math.sin(2 * Math.PI * i / polyNum));     
			leavesVertices[i*6+2] = (float) (leavesRadius * Math.cos(2 * Math.PI * (i+1) / polyNum)); 
			leavesVertices[i*6+3] = (float) (leavesRadius * Math.sin(2 * Math.PI * (i+1) / polyNum)); 
			leavesVertices[i*6+4] = 0;
			leavesVertices[i*6+5] = 0;
		}
	}

	@Override
	protected void drawSelf(Shader shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// Draw Base
		shader.setAttribute("a_position", this.vertexBuffer);	    
		shader.setUniform("u_colour", this.trunkColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.trunkVertices.length / 2);
        
        // Draw H
        shader.setAttribute("a_position", this.leavesVertexBuffer);	    
		shader.setUniform("u_colour", this.leavesColour);	    
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, this.leavesVertices.length / 2);
	}
}
