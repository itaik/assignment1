package comp3170.ass1.sceneobjects;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import comp3170.Shader;

public class SceneObject {

	public Matrix4f localMatrix;
	private Matrix4f worldMatrix;
	private List<SceneObject> children;
	private SceneObject parent;
	
	
	public SceneObject() {
		this.localMatrix = new Matrix4f();
		this.localMatrix.identity();
		
		this.worldMatrix = new Matrix4f();
		this.worldMatrix.identity();
			
		this.parent = null;
		this.children = new ArrayList<SceneObject>();
	}
	
	public void setParent(SceneObject parent) {
		if (this.parent != null) {
			this.parent.children.remove(this);
		}
		
		this.parent = parent;
		
		if (this.parent != null) {
			this.parent.children.add(this);
		}
	}
	
	public Matrix4f getWorldMatrix(Matrix4f matrix) {
		localMatrix.get(matrix);
		
		SceneObject obj = this.parent;
		
		while (obj != null) {
			matrix.mulLocal(obj.localMatrix);
			obj = obj.parent;
		}
		return matrix;
	}
	
	
	protected void drawSelf(Shader shader) {
		// do nothing
	}
	
	public void draw(Shader shader, Matrix4f parentMatrix) {
		
		parentMatrix.get(this.worldMatrix);
		this.worldMatrix.mul(this.localMatrix);
		
		shader.setUniform("u_worldMatrix", this.worldMatrix);
		drawSelf(shader);
	
		for (SceneObject child : children) {
			child.draw(shader, worldMatrix);
		}
		
	}
	
	
	/*
	 * Some methods I made for intuitive access to information used in other parts of the program
	 */
	
	public float getXPosition() {
		return this.localMatrix.get(new float[16])[12];
	}
	
	public float getYPosition() {
		return this.localMatrix.get(new float[16])[13];
	}
	
	public double distanceFrom(SceneObject other) {
		return Math.sqrt(Math.pow((this.getXPosition() - other.getXPosition()), 2) + Math.pow(this.getYPosition() - other.getYPosition(), 2));
	}
	
	public double faceingAngle() {
		return this.localMatrix.getEulerAnglesZYX(new Vector3f()).z;
	}
}
