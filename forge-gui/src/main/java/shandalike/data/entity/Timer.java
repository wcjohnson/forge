package shandalike.data.entity;

import java.util.ArrayList;

import shandalike.Model;
import shandalike.data.entity.thought.ThinkState;
import shandalike.data.entity.thought.Thought;

/**
 * Runs scripts periodically.
 * @author wcj
 */
public class Timer extends Positional {
	float last = 0.0f;
	float period = 0.0f;
	String script;
	
	public static class DoIt implements Thought {
		@Override
		public void think(Entity entity, ThinkState thinkState) {
			// TODO Auto-generated method stub
			Timer e = (Timer)entity;
			if(e.period > 0.0f && (thinkState.gameTime - e.last > e.period)) {
				e.last = thinkState.gameTime;
				Model.script.pcall(e.script, "timer", e);
			}
		}
	}
	
	public Timer() {
		super();
		thoughts = new ArrayList<Thought>();
		thoughts.add(new DoIt());
	}
}
