import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List; 
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Autoplay {
	public static void main(String[] args) {
		GameModel model = new GameModel(false);
		//GameModel model = new GameModel(Driver.post1024Board());
		//SearchSystem.runIterations(model);
		SearchSystem.evaluateAI(model,2048);
		//double test = testFunc();
	}

	
	public static double testFunc(){
		GameModel state = new GameModel(false);
		for(int q =0;q<16;q++){
			state.setTile(q, 0);
		}
		state.setTile(1, 32);
		state.setTile(5, 32);
		double max = 0; //score is how good our current path is (previous tempScore is the max here)
		boolean goalReached = false,goalsOnly = false;
		if(state.myWin){
			return Double.MAX_VALUE;
		}
		if(state.myLose){
			return Double.MIN_VALUE;
		}

		char moves[] = {'R','U','L','D'}; 
		
		int iter = 1;
		
		for(int i = 0; i<4; i++){
			goalReached = false;
			//evaluating score starts
			double tempScore = 0;
			GameModel safeState = (GameModel) state.clone();
			for(int j = 0; j<iter; j++){
				if(j>0) safeState = (GameModel) state.clone();
				double[] evalScore = SearchSystem.evaluateMove(state,safeState,moves[i],false); //new move is attempted and evaluated
				if(evalScore[0]<1){ 
					tempScore = -1;
					break; // move is redundant and so we don't check children
					// OR LOSE
				}else if(evalScore[1]!=-1){ // a goal is reached
					if(!goalsOnly){
						max = 0; //resetting scale
						goalsOnly = true;//1st goal is reached
					}
					goalReached = true; 
					tempScore += evalScore[0];
				}else tempScore += evalScore[0];
			}	
			if(tempScore == -1){ //when accepting only goals and goal isn't reached
				
				continue;
			}else if(goalsOnly&&!goalReached){

				continue;
			}else{
				tempScore/=iter; //avg is found
				tempScore+=0; // the value of this new move in our current path
			}
			//evaluation ends
		
		}
		return max;
	}
}


