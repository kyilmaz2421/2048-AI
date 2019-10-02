
import javax.swing.*;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import java.util.Random;


public class SearchSystem {

	
	private static boolean[] newGoal = new boolean[4];
	private static boolean firstGoal = false;
//	private static boolean highRisk = false;
//	private static boolean firstMax = false;
	
	public static char oneMove(int turn, GameModel gameState, int depth, int  iter){
		char move = '0';
		move = selectMove(turn,depth,gameState,iter);
		GameModel test = (GameModel) gameState.clone();
		makeMove((int)move,false,test);
		
		boolean diff = checkRedundantMove(gameState,test);
		if(!diff){
			System.out.println("FUCKerrere");
			move = //randMove();
					selectMove(turn,1,gameState,iter);
			System.out.println("RANDOM TANOM "+ move);
		}
		return move;
	}
	
	
	
	public static char selectMove(int turn, int depth, GameModel game, int iter){ // greedy search trying to achieve highest score


		char moves[] = {'R','U','L','D'};
		char move = moves[0];
		double max = 0,score = 0,temp=0;
		
		int loseRisk = 0, divisor = iter;
		boolean lose= false;
	
		for(int i = 0; i<4; i++){
			boolean firstGoalFound = false;
			if(lose) break;
			char m = moves[i];
			double avg = 0;
			
			//for(int j=0; j<iter;j++){
				GameModel safeState = (GameModel) game.clone();
				
				int samllIter = 2;
				for(int k = 0; k<2; k++){
					if(k>0) safeState = (GameModel) game.clone();
					double[] evalScore = evaluateMove(game,safeState,moves[i],true); //new move is attempted and evaluated
					if(evalScore[0]<1){ 
						score=0;
						break; // move is redundant and so we don't check children
						// OR LOSE
					}else score += evalScore[0]; //for averaging iterations
				}
				score /= samllIter;
				//= makeMove((int)m,false,safeState);

				if(score==0){// skips a redundant move
					loseRisk++;
					//System.out.println("SKIP"); 
					continue;
				}
				if(safeState.myWin){ 
					 System.out.println("WIIIIN"); 
		   			 makeMove((int)m,true,game);
		   			 return 'w';
		   		}
				if(safeState.myLose){
					loseRisk++;
					//highRisk = true;
					if(loseRisk>3){
						lose = true;
						move = 'x';}
					continue;
					//System.out.println("LOSEE"); 
				}
				if(newGoal[i]&&!firstGoalFound){
			   		//System.out.println( "NEWW MAX " + m);
			   		move = moves[i];
			   		max = 0;
			   		divisor = 1;
			   		firstGoalFound = true;
		   		}
				double[] tempResult = new double[2];
				if(!firstGoalFound){
					tempResult = searchSystem(safeState, depth,loseRisk); //maximizing wins 
			 		//tempResult[0] = search(safeState, depth, score); //maximizing wins 
			 		temp = tempResult[0];
			 		if(tempResult[0]==0) tempResult[0] = score;
			 		else tempResult[0] = score*(tempResult[0]/tempResult[1]); // 1st move * scaled by cloud score
			 		avg+=tempResult[0];
			 		//System.out.println(" Sxcore iteration "+j+ " valu L: "+ tempResult[0]); 
				}
				else if(newGoal[i]){
					//j=iter-1;
			 		tempResult = searchSystem(safeState, depth,loseRisk); //maximizing wins
			 		//tempResult[0] = search(safeState, depth, score); //maximizing wins 
			 		temp = tempResult[0];
			 		if(tempResult[0]==0) tempResult[0] = score; //the score from 1st move
			 		else tempResult[0] = score*(tempResult[0]/tempResult[1]); // 1st move * scaled by cloud score
			 		avg+=tempResult[0];
			 		//System.out.println(" Sxcore iteration "+j+ " valu L: "+ tempResult[0]); 
				}
				else continue;
			//}
			newGoal[i]=false;
			//avg/=divisor;
			System.out.println( "Move " +m+" Rawscore "+score+" Searchscore "+temp+" AvgSccaledScore "+ avg);
	   		if(max<temp){
	   			 max = avg;
	   			 move = m;
	   		 }
	   	 }
//		if(turn%100==0){
			System.out.println("FINAL MAX Score achieved: " +max+ " on MOVE: "+ move);
			
			System.out.println("Turn "+ turn );
//		}
		return move;
	}
	
	
	public static double[] searchSystem(GameModel state, int depth,int loseRisk){
		double[] res = {1.0,0.0};
		boolean goalReached = false,goalsOnly = false;
		if(state.myWin){
			res[0] = Double.MAX_VALUE;
			return res;
		}
		else if(depth==0){
			return res;
		}
		char moves[] = {'R','U','L','D'}; 
		
		int iter = depth-3;
		if(depth<4) iter =2;
		for(int i = 0; i<4; i++){
			//goalReached = false;
			double score = 0;
			GameModel safeState = (GameModel) state.clone();
			for(int j = 0; j<iter; j++){
				if(j>0) safeState = (GameModel) state.clone();
				double[] tempScore = evaluateMove(state,safeState,moves[i],false);
				if(tempScore[0]<1){ 
					if(tempScore[0]==-1){
						res[0] *= 0.98;
						res[1]++; //lose
					}
					score=0;
					break; // move is redundant and so we don't check children
					// OR LOSE
				}	
				else if(tempScore[0]!=-1){ // here
					goalReached = true;	
					if(!goalsOnly) goalsOnly = true;
					score += tempScore[0];
				}// to here
				else score += tempScore[0];
			}	
			if(score == 0||(goalsOnly&&!goalReached)){
				//System.out.println("KSIPP");
				continue;
			}else{
				score/=iter;
				res[1]++; // since we evaluated a NEW move
				res[0] += score; }
			double[] search = searchSystem(safeState, depth-1,loseRisk);
			res[0] +=  search[0];
			res[1] +=  search[1];

		}
		return  res;
	}

	
	public static double[] evaluateMove(GameModel before, GameModel state, char m,boolean firstMove){
		double[] score = {makeMove((int)m,false,state),-1};
		
		HashMap<Integer,Integer> goalMap = new HashMap<Integer,Integer>(); //before
		HashMap<Integer,Integer> valuesFoundMap = new HashMap<Integer,Integer>(); //after
		
		if(score[0] == -1) return score;
		if(score[0] == 0) score[0] = 1;
		
		int[] max = new int[2], tileCount = new int[2];

		boolean initialFound = false, diff = false, goalReached = false;
		int startX=0, startY=0;
		
		if(m =='U') startY = 3;
		if(m =='L') startX = 3;
		//x + y * 4
		for(int x = 0; x<4; x++){
			int val = 0, initialVal = 1;
				for(int y = 0; y<4; y++){
					if(m == 'U'|| m == 'D'){
						if(m == 'U') y*=-1;
						val = before.tileAt(startX + x, startY + y).value;
						//values.add(state.tileAt(startX + x, startY + y).value);
						if(m == 'U') y*=-1;
					}else{
						if(m == 'L') y*=-1;
						val = before.tileAt(startX + y, startY + x).value;
						//values.add(state.tileAt(startX + y, startY + x).value);
						if(m == 'L') y*=-1;
					}
					int beforetile = before.getMyTile(x + y * 4).value;
					int afterTile = state.tileAt(x,y).value;
					max[0] = Math.max(max[0], beforetile);
					max[1] = Math.max(max[1], afterTile);
					if(beforetile!=afterTile) diff = true;
					if(beforetile!=0) tileCount[0]++;  //before
					if(afterTile!=0) tileCount[1]++;  //after
					
					//scan before board to gather info to determine goal
					if(goalMap.containsKey(beforetile)) goalMap.put(beforetile, goalMap.get(beforetile)+1);
					else goalMap.put(beforetile,1);
					
					if(val == 0) continue;
					
					if(!initialFound){
						initialFound = true;
						initialVal = val; 
					}else if(initialFound && val != initialVal){
						initialVal = val; 
					}else if(initialFound && val == initialVal){
						int newval = initialVal*2;
						// all values that will be created by making this move
						if(valuesFoundMap.containsKey(newval)) valuesFoundMap.put(newval, valuesFoundMap.get(newval)+1);
						else valuesFoundMap.put(newval,1);
						
						if(newval>4) score[0] *= (newval);
					}
				}
				initialFound = false; 
		}
		if(!diff){
			score[0] = 0;
			return score;
		}
		// deciding what algorithm should value in its search
		int goalScore = 1;
		int goal = findGoal(goalMap,max[0],tileCount[0],firstMove); // checking if any values created will be the goal
		if(valuesFoundMap.containsKey(goal)){
			goalReached = true;
			score[1] = goal;
			goalScore = valuesFoundMap.get(goal)*goal;
		}
		if(goal == 0 && tileCount[1]<tileCount[0]){
			goalScore = 5*(tileCount[0]-tileCount[1]);
			if(tileCount[1]<13)goalReached = true;
		}
		//System.out.println(goal);
		
		if(tileCount[1]<tileCount[0]&&tileCount[1]>5){
			score[0] *= 5*(tileCount[0]-tileCount[1]); // 2 spaces
		
		if(tileCount[1]<14) score[0] *= 1.25; // 2 spaces
		if(tileCount[1]<13) score[0] *= 1.5;
		if(tileCount[1]<12) score[0] *= 3;
		if(tileCount[1]<11) score[0] *= 4; //5 spaces
		if(max[1]==1024){				
			if(tileCount[1]<14) score[0] *= 1.25;
			if(tileCount[1]<13) score[0] *= 1.5;
			if(tileCount[1]<12) score[0] *= 1.75;
//			if(goalMap.containsKey(512)){
//				if(tileCount[1]<14) score[0] *= 2;
//				if(tileCount[1]<13) score[0] *= 3;
//				if(tileCount[1]<12) score[0] *= 4;
//			}
		}
		}
		
		if(goalReached){ score[0] *= goalScore;
			if(goal>32) score[0] *= goal; //i.e 64,128,etc
			if(max[1]>max[0]) score[0] *= max[1];
			if(firstMove) newGoal[moveToArray(m)]=true;
		}
		
		
		return score;
	}
	
	
	public static boolean checkRedundantMove(GameModel  before,GameModel after){
		//int[] after = state.getMyBoard();
		boolean diff = false;
		for(int j=0; j<16; j++){
			if(before.getMyTile(j)!=after.getMyTile(j)){
				diff = true;
			}
		}
		return diff;  // if "true" board has changed else board is the same
	}

	
	public static double makeMove(int event, boolean notTest,GameModel g) {
		double currScore = (double)g.myScore;
		
		double res = -1;
		if (!g.canMove()) {
			g.myLose = true;
		}
		// we can move
		if (!g.myWin && !g.myLose) {
			switch (event) {
				case (int)'L': //76
					g.left();
					break;
				case (int)'R': //82
					g.right();
					break;
				case (int)'D': //68
					g.down();
					break;
				case (int)'U': //85
					g.up();
					break;
			}
			res = 1;
					//(double)(g.myScore-currScore);
			//res = g.myScore;
		}
		
		// after move is processed assess if theres a lose after
		if (!g.myWin && !g.canMove()) {
			g.myLose = true;
			if(res!=-1) res=-1;
		}
		return res;
	}
	
	public static void printBoard(GameModel g){
		for(int y=0; y<4; y++){
			System.out.print(" -");
		}
		for(int y=0; y<4; y++){
			System.out.println();
			System.out.print("|");
			for(int x=0; x<4; x++){
				System.out.print(g.tileAt(x, y).value+"|");
			}
			System.out.println();
			for(int j=0; j<4; j++){
				System.out.print(" -");
			}
		}
		System.out.println();
	}
	public static char randMove(){
		Random r = new Random();
		//((max-min)+1)+min
		int rand = r.nextInt(6+1);
		switch (rand) {
		case 0:
			return 'L';
		case 1:
			return 'L';
		case 2:
			return 'R';
		case 3:
			return 'U';
		case 4:
			return 'U';
		case 5:
			return 'D';
		case 6:
			return 'D';
		}
		
		return 0;
	}
	
	public static int moveToArray(char m){
		switch ((int)m) {
		case (int)'L': //76
			return 2;
		case (int)'R': //82
			return 0;
		case (int)'D': //68
			return 3;
		case (int)'U': //85
			return 1;
		}
		return -1;
	}
	
	public static int findGoal (HashMap<Integer,Integer> map, int max, int tileCount,boolean highRisk){
		int goal = -1; 
		//if(max==1024&&tileCount>12) return 0;
		//else if(tileCount>13) return 0;
		
		if(max<1024){
			 if(max<512){ //no 512
				 if(map.containsKey(256)){ 
					 if(map.get(256)>1) goal = 512; // new max
					 else goal = findSmallGoals(map,max,tileCount); //find a new 256
				 }else goal = findSmallGoals(map,max,tileCount);// no 256
			 }else if(map.containsKey(512)){ //max is 512 trying to get 1024
				 if(map.get(512)>1) goal = 1024; 
				 else  if(map.containsKey(256)){ // trying to create a 512
					 if(map.get(256)>1) goal = 512; 
					 else goal = findSmallGoals(map,max,tileCount); //find a new 256
				 }else goal = findSmallGoals(map,max,tileCount);// no 256
			 }
		 }else{ //above 1024 
			 if(map.get(1024)>1) goal = 2048; 
			 else if(!map.containsKey(512)){ // no 512 so the goal is 512
				 if(map.containsKey(256)){ 
					 if(map.get(256)>1) goal = 512; 
					 else goal = findSmallGoals(map,max,tileCount); //find a new 256
				 }else goal = findSmallGoals(map,max,tileCount);// no 256
			 }
			 else{ //max is 1024 with a 512 trying to get another 1024 or 512
				 if(map.get(512)>1) goal = 1024; 
				 else if(map.containsKey(256)){ // trying to create a 512
					 if(map.get(256)>1) goal = 512; 
					 else goal = findSmallGoals(map,max,tileCount); //find a new 256
				 }else goal = findSmallGoals(map,max,tileCount);// no 256
			 }	 
		 }

		return goal;
	}
	
	public static int findSmallGoals( HashMap<Integer,Integer> map, int max, int tileCount){
		int goal = 0; 
		int num = 128;
		while(num>2){
			if(map.containsKey(num)) {
				if(map.get(num)>1) return num*2;
				else{
					int smallerNum = (num/2);
					while(smallerNum>2){
						if(map.containsKey(smallerNum)){
							if(map.get(smallerNum)>1) return smallerNum*2;
							else smallerNum /= 2;
						}else smallerNum /= 2;
					}
					return smallerNum;
				}
			}else num /= 2;
		} 
		return goal;
	}
	
	
	public static void evaluateAI(GameModel state,int goal){
		double turnCount = 0;
		int iters = 40, maxTile =0,goalCount = 0, maxTemp = 0;
		for(int i=0; i<iters; i++){
			GameModel gameState = new GameModel(Driver.post1024Board());
			//new GameModel(false);
			char move = ' ';
			int turn =0;
			while(move!='w'||move!='x'){
				move = SearchSystem.oneMove((int) turn,gameState,6, 1);
				if(move!='x'&&move!='w') makeMove((int)move,true,gameState);
				else if(move == 'x'){
					turn++;
					break;
				}else{ // w
					//maxTemp = 2048;
					turn++;
					break;
				}
				//SearchSystem.printBoard(gameState);
				turn++;
			}
			printBoard(gameState);
			maxTemp = gameState.getMaxTile();
			if(maxTemp==goal) goalCount++;
			//maxTile = Math.max(maxTile, maxTemp);
			turnCount += turn;
			System.out.println("GAME "+i+ " COMPLEYE with max "+ maxTemp);
		}
		turnCount/=iters;
		System.out.println("GAMES DONE: "+goalCount +"/"+iters + " successes WITH AVGE TURN count " + turnCount );
		
	}
	
	public static void runIterations(GameModel gameState){
		int maxScore=0, maxTile =0, avgTestMax = 0, avgMaxTotal = 0, turn =0, wins = 0, winsTotal = 0, worstTile =0;
		int iter = 9;
		for(int i=0; i<iter; i++){
			winsTotal = 0;
			System.out.println(" DEPTH "+ (i+1));
			for(int j=150; j < 151; j+=100){
				wins = 0;
				worstTile = 2048;
				char move = ' ';
				for(int test =0; test<2; test++){
					turn = 0;
					while(move!='w'||move!='x'){
 						move = SearchSystem.oneMove((int) turn,gameState,i+1,j);
						if(move!='x') makeMove((int)move,true,gameState);
						else if(move == 'x'){
							turn++;
							break;
						}
						//SearchSystem.printBoard(gameState);
						turn++;
					}
					maxTile = gameState.getMaxTile();
					worstTile = Math.min(maxTile, worstTile);
					//System.out.println("   ITER test#: "+test +" has "+maxTile+ " WORST TILE: "+ worstTile);
					if(maxTile>=2048) wins++;
					avgMaxTotal += maxTile;
					if(test!=1) gameState.resetGame();
				 }
					avgMaxTotal /= 2;
					maxTile = gameState.getMaxTile();
					System.out.println("    IT "+ j  +" turn "+ turn +" THE SCORE: " +gameState.myScore+ " BEST TILE: "+ maxTile + " WORST TILE: "+ worstTile+ " AVGmaxx "+ avgMaxTotal +" Win: " +wins );
					maxScore = Math.max(maxTile, maxScore);
					gameState.resetGame();
					avgTestMax+=avgMaxTotal;
			}
			avgTestMax/=2;
			System.out.println("AVG SCORE :" + avgTestMax +" Win? : " +winsTotal);
			System.out.println();
		}
		System.out.println("");
		System.out.println("OVER ALL BEST TILE:  "+ maxScore);
	}
	
	public static double search(GameModel state, int depth, double score){
		double max = score; //score is how good our current path is (previous tempScore is the max here)
		boolean goalReached = false,goalsOnly = false;
		if(state.myWin){
			return Double.MAX_VALUE;
		}
		if(state.myLose){
			return Double.MIN_VALUE;
		}
		if(depth == 0){
			return score; //returns the previous tempScore propagating it back up  
		}

		char moves[] = {'R','U','L','D'}; 
		
		int iter = 3;
		if(depth<4) iter = 2;
		for(int i = 0; i<4; i++){
			goalReached = false;
			//evaluating score starts
			double tempScore = 0;
			GameModel safeState = (GameModel) state.clone();
			for(int j = 0; j<iter; j++){
				if(j>0) safeState = (GameModel) state.clone();
				double[] evalScore = evaluateMove(state,safeState,moves[i],false); //new move is attempted and evaluated
				if(evalScore[0]<1){ 
					tempScore = -1;
					break; // move is redundant and so we don't check children
					// OR LOSE
				}else if(evalScore[1]>8){ // a goal is reached
					if(!goalsOnly){
						//max = 0; //resetting scale
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
				tempScore+=score; // the value of this new move in our current path
			}
			//evaluation ends
			
			double searchScore = search(safeState,depth-1,tempScore)*depth;
			max = Math.max(max, searchScore); // on 1st iter the search must beat atleast the score from previous path
		}
		return max;
}


}
