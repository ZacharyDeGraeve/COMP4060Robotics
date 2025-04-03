package GDXaiSample;

import com.badlogic.gdx.ai.btree.*;
import com.badlogic.gdx.ai.btree.branch.*;

// You probably want to put much of your robot control and state in here, and expose
// methods that are then called by the behavior tree. This simplifies the tree and keeps
// relevant code separated.
class Blackboard {
	String message;
}

// Simple Action Node that logs a message
class LogAction extends LeafTask<Blackboard> {
    private final String message;

    public LogAction(String message) {
        this.message = message;
    }

    @Override
    public Status execute() {
        System.out.println(message);
        return Status.SUCCEEDED;
    }
	
	//// You need to implement this to support leaf and tree cloning and copying. You may never do it,
	///   but the API requires it. So for now, just put minimal effort into getting it to compile
	///   unless you start copying and cloning your subtrees.
    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return new LogAction(this.message);
    }
}

// Simple Action node that makes a random result and shares it in the blackboard
class RandomResult extends LeafTask<Blackboard> {

    public RandomResult() {
    }

    @Override
    public Status execute() {
        int rand = (int)(Math.random()*3);
		Status result = Status.RUNNING;
		if (rand == 0)
			result =  Status.SUCCEEDED;
		else if (rand == 1)
			result = Status.FAILED;
		
		// save the result in our blackboard
		Blackboard bb = getObject(); // get the blackboard
		bb.message = result.toString();
		System.out.println("Random Result: "+result.toString());
		return result; 
    }
	
	//// You need to implement this to support leaf and tree cloning and copying. You may never do it,
	///   but the API requires it. So for now, just put minimal effort into getting it to compile
	///   unless you start copying and cloning your subtrees.
    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return new RandomResult();
    }
}

// Simple Action node that makes a random result and shares it in the blackboard
class PrintResult extends LeafTask<Blackboard> {

    public PrintResult() {
    }

    @Override
    public Status execute() {
		Blackboard bb = getObject(); // get the blackboard
        System.out.println("Print result: "+bb.message);
		return Status.SUCCEEDED; 
    }
	
	//// You need to implement this to support leaf and tree cloning and copying. You may never do it,
	///   but the API requires it. So for now, just put minimal effort into getting it to compile
	///   unless you start copying and cloning your subtrees.
    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return new PrintResult();
    }
}

public class GDXaiSample {

    public static BehaviorTree<Blackboard> makeTree(Blackboard bb) {
		@SuppressWarnings("unchecked")  // just to suppress the annoying message
		BehaviorTree<Blackboard> tree = new BehaviorTree<>(new Selector<>(
			new Sequence<>(
				new LogAction("Step 1: Checking condition..."),
				new RandomResult() 
			),
			new Sequence<>(
				new LogAction("Step 2: Fallback action executed"),
				new PrintResult() 
			)
		));
		tree.setObject(bb);
		return tree;
	}


	void run() {

		// shared data structure to communicate within the tree. You may want to make your own robot state object
		Blackboard blackboard = new Blackboard();

		// Create a behavior tree manually
		// the BehaviorTree generic type is the blackboard type it expects
		BehaviorTree<Blackboard> tree = makeTree(blackboard);
	
		final int MAX_TICKS = 10;
		int ticks = 0;
		while (ticks < MAX_TICKS) {
			// System.out.print("\033[H");  // move to origin
			
			System.out.println("\nTick: "+ticks+" Start");
			tree.step();  // tick the tree
			
			System.out.println("Tick: "+ticks+" status: "+tree.getStatus().toString());
			
			// System.out.flush();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ticks++;
		}
	}

	public static void main(String args[]){
		GDXaiSample sample = new GDXaiSample();
		sample.run();
	}

}
