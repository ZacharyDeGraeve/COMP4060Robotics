package GDXaiSample;

import com.badlogic.gdx.ai.btree.*;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.ai.btree.branch.*;


class SubTreeTask<T> extends LeafTask<T> {
	private BehaviorTree<T> subtree;

	public SubTreeTask(BehaviorTree<T> subtree) {
		this.subtree = subtree;
	}

	@Override
	public Status execute() {
		subtree.step();
		return subtree.getStatus();
	}

	@Override
    protected Task<T> copyTo(Task<T> task) {
        return new SubTreeTask<>(subtree);
    }
}



public class GDXaiSubTree {

	void run() {

		// shared data structure to communicate within the tree. You may want to make your own robot state object
		Blackboard blackboard = new Blackboard();

		BehaviorTree<Blackboard> testTree = GDXaiSample.makeTree(blackboard);

		// Create a behavior tree manually
		// the BehaviorTree generic type is the blackboard type it expects
		@SuppressWarnings("unchecked")  // just to suppress the annoying message
		BehaviorTree<Blackboard> tree = new BehaviorTree<>(
			new Sequence<>(
				new LogAction("New Tree, before calling subtree..."),
				new SubTreeTask<Blackboard>(testTree)
			)
		);
		tree.setObject(blackboard);
	
		final int MAX_TICKS = 10;
		int ticks = 0;
		while (ticks < MAX_TICKS) {
			// System.out.print("\033[H");  // move to origin
			
			System.out.println("\nTick: "+ticks+" Start");
			tree.step();  // tick the tree
			
			Status status = tree.getChild(0).getStatus();
			System.out.println("Tick: "+ticks+" status: "+status.toString());
			
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
		GDXaiSubTree sample = new GDXaiSubTree();
		sample.run();
	}

}
