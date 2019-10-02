
import javax.swing.*;



import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List; 

public class GameModel extends JPanel {
	
	private Tile[] myTiles;
	public boolean myWin = false;
	public boolean myLose = false;
	public int myScore = 0;
	
	public GameModel(boolean view) {
		if(view){
			setPreferredSize(new Dimension(340, 400));
			setFocusable(true);
		}
		resetGame();	
	}
	
	public GameModel(GameModel g) {
		super();
		//,Tile[] tiles, int score, boolean lose, boolean win
		setPreferredSize(new Dimension(340, 400));
		setFocusable(true);
		
		this.myTiles = new Tile[16];
		for(int i=0; i<16;i++){
			this.myTiles[i] = new Tile(g.myTiles[i].value);
		}
		this.myScore = g.myScore;
		this.myWin = g.myWin;
		this.myLose = g.myLose;
	}

	public Object clone() {
	        return new GameModel(this);
	   }
	
	public Object createState(GameModel state) {
        return new GameModel(state);
   }
	
	public int[] getMyBoard(){
		int[]  board = new int[16];
		for(int i=0; i<16; i++){
			board[i] = this.getMyTile(i).value;
		}
		return board;
	}
	
	
	public void resetGame() {
		myScore = 0;
		myWin = false;
		myLose = false;
		myTiles = new Tile[4 * 4];
		for (int i = 0; i < myTiles.length; i++) {
			myTiles[i] = new Tile();
		}
		addTile();
		addTile();
	}
	public void setTile(int location, int value){
		myTiles[location].value = value;
	}
	
	public Tile getMyTile(int x){
		return myTiles[x];
	}
	
	public void left() {
		boolean needAddTile = false;
		for (int i = 0; i < 4; i++) {
			Tile[] line = getLine(i);
			Tile[] merged = mergeLine(moveLine(line));
			setLine(i, merged);
			if (!needAddTile && !compare(line, merged)) {
				needAddTile = true;
			}
		}
		
		if (needAddTile) {
			addTile();
		}
	}
	
	public void right() {
		myTiles = rotate(180);
		left();
		myTiles = rotate(180);
	}
	
	public void up() {
		myTiles = rotate(270);
		left();
		myTiles = rotate(90);
	}
	
	public void down() {
		myTiles = rotate(90);
		left();
		myTiles = rotate(270);
	}
	
	public Tile tileAt(int x, int y) {
		return myTiles[x + y * 4];
	}
	
	private void setLine(int index, Tile[] re) {
		System.arraycopy(re, 0, myTiles, index * 4, 4);
	}
	
	private Tile[] getLine(int index) {
		Tile[] result = new Tile[4];
		for (int i = 0; i < 4; i++) {
			result[i] = tileAt(i, index);
		}
		return result;
	}
	private Tile[] moveLine(Tile[] oldLine) {
		LinkedList<Tile> l = new LinkedList<Tile>();
		for (int i = 0; i < 4; i++) {
			if (!oldLine[i].isEmpty())
				l.addLast(oldLine[i]);
		}
		if (l.size() == 0) {
			return oldLine;
		}
		else {
			Tile[] newLine = new Tile[4];
			ensureSize(l, 4);
			for (int i = 0; i < 4; i++) {
				newLine[i] = l.removeFirst();
			}
			return newLine;
		}
	}
	
	private Tile[] mergeLine(Tile[] oldLine) {
		LinkedList<Tile> list = new LinkedList<Tile>();
		for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
			int num = oldLine[i].value;
			if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
				num *= 2; //the new value being created by a combo
				myScore += num;
				int ourTarget = 2048;
				if (num == ourTarget) {
					myWin = true;
				}
				i++;
			}
			list.add(new Tile(num));
		}
		if (list.size() == 0) {
			return oldLine;
		}
		else {
			ensureSize(list, 4);
			return list.toArray(new Tile[4]);
		}
	}
	
	private void ensureSize(java.util.List<Tile> l, int s) {
		while (l.size() != s) {
			l.add(new Tile());
		}
	}
	
	private void addTile() {
		List<Tile> list = availableSpace();
		if (!availableSpace().isEmpty()) {
			int index = (int) (Math.random() * list.size()) % list.size();
			Tile emptyTile = list.get(index);
			emptyTile.value = Math.random() < 0.9 ? 2 : 4;
		}
	}
	
	private List<Tile> availableSpace() {
		final List<Tile> list = new ArrayList<Tile>(16);
		for (Tile t : myTiles) {
			if (t.isEmpty()) {
				list.add(t);
			}
		}
		return list;
	}
	
	private boolean isFull() {
		return availableSpace().size() == 0;
	}
	
	public boolean canMove() {
		if (!isFull()) {
			return true;
		}
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				Tile t = tileAt(x, y);
				if ((x < 3 && t.value == tileAt(x + 1, y).value) || ((y < 3) && t.value == tileAt(x, y + 1).value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean compare(Tile[] line1, Tile[] line2) {
		if (line1 == line2) {
			return true;
		}
		else if (line1.length != line2.length) {
			return false;
		}
		
		for (int i = 0; i < line1.length; i++) {
			if (line1[i].value != line2[i].value) {
				return false;
			}
		}
		return true;
	}
	
	private Tile[] rotate(int angle) {
		Tile[] newTiles = new Tile[4 * 4];
		int offsetX = 3, offsetY = 3;
		if (angle == 90) {
			offsetY = 0;
		}
		else if (angle == 270) {
			offsetX = 0;
		}
		
		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				int newX = (x * cos) - (y * sin) + offsetX;
				int newY = (x * sin) + (y * cos) + offsetY;
				newTiles[(newX) + (newY) * 4] = tileAt(x, y);
			}
		}
		return newTiles;
	}
	
	public int getMaxTile(){
		int max = 0;
		for(int i=0; i< 16; i++){
			max =  Math.max(max, myTiles[i].value);
		}
		return max;
	}
}