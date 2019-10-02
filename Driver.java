

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

// Kaan Yilmaz
// 260706265


public class Driver {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		GameModel model = new GameModel(post1024Board());
		//GameModel model = new GameModel(true);
		
		frame.setTitle("2048 Game");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(340, 400);
		frame.setResizable(true);
		
		GamePanel view = new GamePanel(model);
		Controller control = new Controller(model,view);
		
		frame.add(view);
		frame.addKeyListener(control);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	
	public static GameModel post1024Board(){
		GameModel model = new GameModel(true);
		Random r = new Random();
		//((max-min)+1)+min
		int rand1024 = r.nextInt(15+1);
		int rand128 = r.nextInt(15+1);
//		while(rand128==rand1024){
//			rand128 = r.nextInt(15+1);
//		}
		model.setTile(rand1024,1024);
		//model.setTile(rand128,512);
		int tileCount = r.nextInt(14+8);
		//System.out.println(tileCount);
		for(int i=0; i<13; i++){
			int spot = r.nextInt(15+1);
			//System.out.println(spot);
			if(model.getMyTile(spot).value==0){
				int value = randTile();
				model.setTile(spot,value);
			}else{
				i--;
				continue;
			}
		}
		return model;
	}
	
	public static int randTile(){
		Random r = new Random();
		//((max-min)+1)+min
		int rand = r.nextInt(14+1);
		switch (rand) {
		case 0:
			return 2;
		case 1:
			return 4;
		case 2:
			return 32;
		case 3:
			return 16;
		case 4:
			return 32;
		case 5:
			return 8;
		case 6:
			return 2;
		case 7:
			return 16;
		case 8:
			return 2;
		case 9:
			return 2;
		case 10:
			return 4;
		case 11:
			return 4;
		case 12:
			return 2;
		case 13:
			return 8;
		case 14:
			return 16;
		}
		
		return 0;
	}

	
	
}
