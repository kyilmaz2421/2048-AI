

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Controller extends KeyAdapter {
	private GameModel prevState;
	private GameModel gameState;
	private int enterPressCount = 0;
	private GamePanel view;
	private SearchSystem search;
	
	public Controller(GameModel g, GamePanel view) {
		gameState = g;
		this.view = view;
		//System.out.println("OGG BOARD!");
		SearchSystem.printBoard(g);
	}
	
	@Override	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			enterPressCount=0;
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			control(-1,null);
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			prevState = (GameModel) gameState.clone();
			control(0,null);
			enterPressCount++;
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			prevState = (GameModel) gameState.clone();
			control(1,null);
			enterPressCount++;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			prevState = (GameModel) gameState.clone();
			control(2,null);
			enterPressCount++;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			prevState = (GameModel) gameState.clone();
			control(3,null);
			enterPressCount++;
		}
		else if(e.getKeyCode() == KeyEvent.VK_8){
			control(8,gameState);
		}
		else if(e.getKeyCode() == KeyEvent.VK_9){
			control(9,prevState);
		}
		else{ //(e.getKeyCode() == KeyEvent.VK_ENTER){
			char move = SearchSystem.oneMove(enterPressCount,gameState,7,1); //depth , iters
			prevState = (GameModel) gameState.clone();
			SearchSystem.makeMove((int)move,true,gameState);
			view.repaint();
			enterPressCount++;
		}
	}
	
	public void control(int val,GameModel newModel){
		switch(val){
		case -1:
			gameState.resetGame();
			System.out.println("NEW BOARD!");
			break;
		case 0:
			System.out.println("Right");
			SearchSystem.makeMove((int)'R',true,gameState);
			break;
		case 1:
			System.out.println("Up");
			SearchSystem.makeMove((int)'U',true,gameState);
			break;
		case 2:
			System.out.println("Left");
			SearchSystem.makeMove((int)'L',true,gameState);
			break;
		case 3:
			System.out.println("Down");
			SearchSystem.makeMove((int)'D',true,gameState);
			break;
		case 9:
			System.out.println("Last STATE");
			gameState = (GameModel) gameState.createState(newModel);
			view.repaint();
			break;
		case 8:
			System.out.println("STATE restored");
			gameState = (GameModel) gameState.createState(newModel);
			view.repaint();
			break;
			
		}
		view.repaint();
	}
	


}
