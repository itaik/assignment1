
/*
 * Itai Ktalav
 * 4368938190
 */

package comp3170.ass1;

import java.awt.event.KeyEvent;

/*
 *  A container class for keeping track of key presses
 *  Figured this was neater than having all this in the main Assignment1 class
 */
public class KeyManager {
	
	private boolean upKeyDown;
	
	private boolean leftKeyDown; 
	
	private boolean rightKeyDown;
	
	private boolean lKeyDown;
	
	private boolean tKeyDown;
	
	private int mouseX = -1;
	
	private int mouseY = -1;
	
	public KeyManager() {
		upKeyDown = false;
		leftKeyDown = false;
		rightKeyDown = false;
		lKeyDown = false;
		tKeyDown = false;
	}
	
	public boolean upKeyIsDown()    { return upKeyDown; }
	public boolean leftKeyIsDown()  { return leftKeyDown; }
	public boolean rightKeyIsDown() { return rightKeyDown; }
	public boolean lKeyIsDown()     { return lKeyDown; }
	public boolean tKeyIsDown()     { return tKeyDown; }
	public int     getMouseX()      { return mouseX; }
	public int     getMouseY()      { return mouseY; }

	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_UP) {
			upKeyDown = true;
		}
		else if (keyCode == KeyEvent.VK_LEFT) {
			leftKeyDown = true;
		}
		else if (keyCode == KeyEvent.VK_RIGHT) {
			rightKeyDown = true;
		}
		else if (keyCode == KeyEvent.VK_L) {
			lKeyDown = true;
		}
		else if (keyCode == KeyEvent.VK_T) {
			tKeyDown = true;
		}
	}

	public void keyReleased(int keyCode) {
		if (keyCode == KeyEvent.VK_UP) {
			upKeyDown = false;
		}
		else if (keyCode == KeyEvent.VK_LEFT) {
			leftKeyDown = false;
		}
		else if (keyCode == KeyEvent.VK_RIGHT) {
			rightKeyDown = false;
		}
		else if (keyCode == KeyEvent.VK_L) {
			lKeyDown = false;
		}
		else if (keyCode == KeyEvent.VK_T) {
			tKeyDown = false;
		}
	}
	
	public void setMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
	}
	
	public void resetMouse() {
		mouseX = -1;
		mouseY = -1;
	}
	
	/*
	 * If the mouse coordinates are set
	 */
	public boolean mouseSet() {
		return mouseX != -1 && mouseY != -1;
	}
	
	

}
