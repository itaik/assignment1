
/*
 * Itai Ktalav
 * 43681913
 */

package comp3170.ass1;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.joml.Matrix4f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import comp3170.GLException;
import comp3170.InputManager;
import comp3170.Shader;
import comp3170.ass1.sceneobjects.HeliPad;
import comp3170.ass1.sceneobjects.Helicopter;
import comp3170.ass1.sceneobjects.House;
import comp3170.ass1.sceneobjects.River;
import comp3170.ass1.sceneobjects.SceneObject;
import comp3170.ass1.sceneobjects.Tree;

public class Assignment1 extends JFrame implements GLEventListener, KeyListener, MouseListener {

	private static final long serialVersionUID = -4428934514676903091L; // Generated...

	private GLCanvas canvas;
	private Shader shader;
	
	final private File DIRECTORY = new File("src/comp3170/ass1"); 
	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private InputManager input;
	
	private KeyManager keyManager;
	
	private Animator animator;
	
	private SceneObject root;	

	private Matrix4f worldMatrix;
	
	// Track the time of most recent animation update.
	private long oldTime;
	
	// Scene objects
	
	private Helicopter helicopter;
	
	private HeliPad heliPad;
	
	public Assignment1() {
		super("COMP3170 Assignment 1: Itai Ktalav 4368938190");
		
		// create an OpenGL 4 canvas and add this as a listener
		
		GLProfile profile = GLProfile.get(GLProfile.GL4);		 
		GLCapabilities capabilities = new GLCapabilities(profile);
		this.canvas = new GLCanvas(capabilities);
		this.canvas.addGLEventListener(this);
		this.add(canvas);
		
		// create an input manager to listen for keypresses and mouse events
		
		input = new InputManager();
		input.addListener(this);
		input.addListener(this.canvas);
		
		// set up the key manager
		keyManager = new KeyManager();

		// set up the JFrame		
		
		this.setSize(1000, 1000);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});	
		
		// Listen for key and mouse events
		
		this.addKeyListener(this);
		canvas.addKeyListener(this);
		this.addMouseListener(this);
		canvas.addMouseListener(this);

		// Add an animator to regularly redraw the screen and respond to key states
		
		this.animator = new Animator(canvas);
		this.animator.start();
		
	}

	@Override
	/**
	 * Initialise the GLCanvas
	 * Add Objects to the scene
	 */
	public void init(GLAutoDrawable drawable) {
		//GL4 gl = (GL4) GLContext.getCurrentGL();
		
		// Compile the shader
		try {
			File vertexShader = new File(DIRECTORY, VERTEX_SHADER);
			File fragementShader = new File(DIRECTORY, FRAGMENT_SHADER);
			this.shader = new Shader(vertexShader, fragementShader);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (GLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// allocate matrices
		
		this.worldMatrix = new Matrix4f();
		
		// construct objects and attach to the scene-graph
		this.root = new SceneObject();
		
		// Create river
		new River(this.shader, this.root);
		
		// Create village
		new House(this.shader, this.root, 14, -8);
		new House(this.shader, this.root, 16, 2);
		new House(this.shader, this.root, 10, -2);
		new House(this.shader, this.root, -15f, 5.5f);
		new House(this.shader, this.root, -16, -3f);
		new House(this.shader, this.root, -10, 1f);
		
		// Create small forest
		new Tree(this.shader, 10, this.root, -1.45f, 9.5f);
		new Tree(this.shader, 5,  this.root, -2f,    7f);
		new Tree(this.shader, 9,  this.root, -1.5f,  5f);
		new Tree(this.shader, 6,  this.root, -0.2f,  8f);
		new Tree(this.shader, 7,  this.root,  1f,    9f);
		new Tree(this.shader, 8,  this.root,  1f,    5.5f);
		
		// Create helipad
		heliPad = new HeliPad(this.shader, this.root, -0.5f, -4f);
		
		// Create helicopter
		helicopter = new Helicopter(this.shader, this.root, heliPad.getXPosition(), heliPad.getYPosition());
	}

	@Override
	/**
	 * Called when the canvas is redrawn
	 */
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		// update the scene
		update();
		
		// set the background colour to dark green
		gl.glClearColor(0.1f, 0.6f, 0.1f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT);		
		
		
		this.shader.enable();
		
		//FloatBuffer fb_origin = Buffers.newDirectFloatBuffer(origin);
		//gl.glUniform2fv(shader.getUniform("u_origin"), 1, fb_origin);
		
		this.worldMatrix.identity();
		this.root.draw(shader, worldMatrix);

	}

	/*
	 * Called in the above display() method
	 * Handles all logic and operations relating to helicopter movement and user events such as mouse clicks or key presses
	 */
	private void update() {
		// calculate how much time has passed since the last update
		long time = System.currentTimeMillis();			// ms
		float deltaTime = (time - oldTime) / 1000f;		// seconds
		oldTime = time;	
		helicopter.setDeltaTime(deltaTime);
		
		// If the mouse has been pressed, set the location for the helicopter to head to
		if (keyManager.mouseSet()) {
			helicopter.setDestination(keyManager.getMouseX(), keyManager.getMouseY());
			keyManager.resetMouse();
		}
		
		helicopter.setMove(keyManager.upKeyIsDown()); // Moves the helicopter forward if the up key is down
		// Change helicopter direction if the left of right keys are pressed
		if (keyManager.leftKeyIsDown()) {
			helicopter.rotate(true);
		}
		if (keyManager.rightKeyIsDown()) {
			helicopter.rotate(false);
		}
		
		helicopter.rotateRotors(deltaTime); // Keeps the rotors spinning 
		
		// Checks to see if the helicopter is in range to land
		if (helicopter.distanceFrom(heliPad) < 0.05) { // Found this number through trial and error 
			heliPad.setOkToLand(true);
			
			if (!helicopter.landing() && keyManager.lKeyIsDown()) {
				helicopter.land();
			}
			else if (helicopter.landing() && keyManager.tKeyIsDown()) {
				helicopter.takeOff();
			}
		}
		else {
			heliPad.setOkToLand(false);
		}
	}

	@Override
	/**
	 * Called when the canvas is resized
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glViewport(0, 0, width, height);
		//gl.glViewport(x, y, width, height);

	}

	@Override
	/**
	 * Called when we dispose of the canvas 
	 */
	public void dispose(GLAutoDrawable drawable) {

	}

	public static void main(String[] args) throws IOException, GLException {
		new Assignment1();
	}

	
	// User event catcher methods 
	
	@Override
	public void keyPressed(KeyEvent e) { keyManager.keyPressed(e.getKeyCode()); }
	@Override
	public void keyReleased(KeyEvent e) { keyManager.keyReleased(e.getKeyCode()); }

	@Override
	public void mouseClicked(MouseEvent e) { keyManager.setMouse(e.getX(), e.getY()); }
	
	// These methods don't need to do anything, but they do need to be here
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}


}
