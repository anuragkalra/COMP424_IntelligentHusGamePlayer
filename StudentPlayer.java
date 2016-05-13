package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Random;

import student_player.mytools.MyTools;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {
	
	public int nodesEvaluated = 0;

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260631195"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
        // Get the contents of the pits so we can use it to make decisions.
        //int[][] pits = board_state.getPits();/

        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
        //int[] my_pits = pits[player_id];
        //int[] op_pits = pits[opponent_id];

        // Use code stored in ``mytools`` package.
        MyTools.getSomething();

        // Get the legal moves for the current board state.
        //ArrayList<HusMove> moves = board_state.getLegalMoves();
        //HusMove move = moves.get(0);
        //float result = alphaBeta(board_state, 2, -10000, 10000, true);
        //System.out.println(result);
        //ArrayList<HusBoardState> children = helperSuccessors(board_state);
        
        HusMove move = alphaBetaPackager(board_state, 5, -1000, 1000);
        
        
        // We can see the effects of a move like this...
        //HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
        //cloned_board_state.move(move);

        // But since this is a placeholder algorithm, we won't act on that information.
        return move;
    }
    
    public HusMove alphaBetaPackager(HusBoardState n, int depth, float alpha, float beta){
    	//System.out.println(nodesEvaluated);
    	HBNode nNode = new HBNode();
    	nNode.setH(n);
    	
    	ArrayList<HBNode> depth1Nodes = helperSuccessorsNode(nNode);	//sets up depth 1 layer
    	
    	//int k = 0;
    	for(HBNode d1Node: depth1Nodes){
    		d1Node.setKids(helperSuccessorsNode(d1Node));	//sets up depth 2 layer
    		float i = 10000.0f;
    		for(HBNode d2Node: d1Node.getKids()){
    			d2Node.setEvaluation(alphaBeta(d2Node.getH(), depth-2, alpha, beta, true));
    			if(d2Node.getEvaluation() < i){
    				i = d2Node.getEvaluation();
    			}
    		}
    		
    		d1Node.setEvaluation(i);
    	}
    	
    	float total = 0;
    	int count = 0;
    	for(int i = 0; i < depth1Nodes.size(); i++){
    		for(int j = 0; j < depth1Nodes.get(i).getKids().size(); j++){
    			float val = depth1Nodes.get(i).getKids().get(j).getEvaluation();
    			total += val;
    			count++;
    			//System.out.print(val + " ");
    		}
    		//System.out.println("\t");
    	}
    	//System.out.println("\nAverage = " + (total/count));
    	
    	eval_and_bestMove DECISION = new eval_and_bestMove();
    	DECISION.setEval(-10000.0f);
    	
    	for(int i = 0; i < depth1Nodes.size(); i++){
    		if(depth1Nodes.get(i).getEvaluation() > DECISION.getEval()){
    			DECISION.setEval(depth1Nodes.get(i).getEvaluation());
    			DECISION.setIndex(i);
    		}    		
    	}
    	
    	
    	HusMove a = n.getLegalMoves().get(DECISION.getIndex());
    	//System.out.println(nodesEvaluated);
    	return a;
    }
    
    public float alphaBeta(HusBoardState h, int depth, float alpha, float beta, boolean isMax){
    	
    	if ((depth == 0) || h.gameOver()) {
    		nodesEvaluated++;
    		return helperEval(h);
    	}
    	
    	if(isMax == true){
    		float v = -10000.0f;
    		ArrayList<HusBoardState> children = helperSuccessors(h);
    		for(HusBoardState hMin: children){
    			float z = alphaBeta(hMin, depth - 1, alpha, beta, false);
    			v = Math.max(v, z);
    			alpha = Math.max(alpha, v);
    			if (beta <= alpha){
    				break;
    			}
    		}
    		return v;
    	}

    	else{  //isMax == false
    		float v = 10000.0f;
    		ArrayList<HusBoardState> children = helperSuccessors(h);
    		for(HusBoardState hMax: children){
    			float z = alphaBeta(hMax, depth - 1, alpha, beta, true);
    			v = Math.min(v, z);
    			beta = Math.min(beta, v);
    			if (beta <= alpha){
    				break;
    			}
    		}
    		return v;
    	}

    }
    
    public ArrayList<HBNode> helperSuccessorsNode(HBNode n){
    	ArrayList<HusMove> moves = n.getH().getLegalMoves();
    	ArrayList<HusBoardState> childrenStates = helperSuccessors(n.getH());
		ArrayList<HBNode> children = new ArrayList<HBNode>();
		for(int i = 0; i < moves.size(); i++){	//fills up children with empty HBNodes
			HBNode a = new HBNode();
			children.add(a);
		}
		int childrenSize = children.size();
		for(int j = 0; j < childrenSize; j++){	//fills in clone of each state into node.h
			children.get(j).setH(childrenStates.get(j));
    	}
		return children;
    }
    
	public ArrayList<HusBoardState> helperSuccessors(HusBoardState h) {
		ArrayList<HusMove> moves = h.getLegalMoves();
		ArrayList<HusBoardState> children = new ArrayList<HusBoardState>();
		
		for(HusMove mv: moves){
			HusBoardState cloned_board_state = (HusBoardState) h.clone();
			cloned_board_state.move(mv);
			children.add(cloned_board_state);
		}
		return children;
	}

	public float helperEval(HusBoardState h) {
		int a = totalSeeds(h);
		int b = legalMovesSize(h);
		
		Random randomno = new Random();		 
		float fuzz = randomno.nextFloat();
		
		float eval = (float) (a + fuzz);	//a works better
		
		return eval;
	}
	
	public int totalSeeds(HusBoardState h){
		int[][] pits = h.getPits();
		int[] myPits = pits[player_id];
		int sum = 0;
		for(int i = 0; i < myPits.length; i++){
			sum += myPits[i];
		}
		return sum;
	}
	
	public int legalMovesSize(HusBoardState h){
		int size = h.getLegalMoves().size();
		return size;
	}
}

class HBNode{
	HusBoardState h;	
	int index;
	float evaluation;
	ArrayList<HBNode> kids = new ArrayList<HBNode>();
	
	public HBNode(HusBoardState h, int index, float evaluation) {
		this.h = h;
		this.index = index;
		this.evaluation = evaluation;
	}
	public HBNode() {
	}
	public HusBoardState getH() {
		return h;
	}
	public int getIndex() {
		return index;
	}
	public float getEvaluation() {
		return evaluation;
	}
	public ArrayList<HBNode> getKids() {
		return kids;
	}	
	public void setH(HusBoardState h) {
		this.h = h;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setEvaluation(float evaluation) {
		this.evaluation = evaluation;
	}
	public void setKids(ArrayList<HBNode> kids) {
		this.kids = kids;
	}
}

class eval_and_bestMove{
	float eval;
	int index;
	public float getEval() {
		return eval;
	}
	public int getIndex() {
		return index;
	}
	public void setEval(float eval) {
		this.eval = eval;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
