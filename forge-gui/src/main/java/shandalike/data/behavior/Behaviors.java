package shandalike.data.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of Behaviors that can be safely mutated during iteration. All mutations are carried out
 * after iteration is complete.
 * Note: this is not a true concurrent collection; don't even think about using it on multiple threads.
 * @author wcj
 */
public class Behaviors implements Iterable<Behavior>, Collection<Behavior>, IBehavioral {
	private class BehIterator implements Iterator<Behavior> {
		int pos = 0;
		
		public BehIterator() {
			iterDepth++;
		}
		
		@Override
		public boolean hasNext() {
			if(pos < behaviors.size()) {
				return true;
			} else {
				iterDepth--;
				if(iterDepth == 0) flushUpdates();
				return false;
			}
		}

		@Override
		public Behavior next() {
			pos++;
			return behaviors.get(pos - 1);
		}
		
	}
	
	List<Behavior> behaviors = new ArrayList<Behavior>();
	
	transient List<Behavior> additions = null;
	transient List<Object> removals = null;
	transient int iterDepth = 0;
	
	private boolean isIterating() { return (iterDepth > 0); }
	
	private void flushUpdates() {
		if(removals != null && removals.size() > 0) {
			for(Object removal: removals) behaviors.remove(removal);
			removals.clear();
		}
		if(additions != null && additions.size() > 0) {
			for(Behavior addition: additions) behaviors.add(addition);
			additions.clear();
		}
	}

	@Override
	public Iterator<Behavior> iterator() {
		return new BehIterator();
	}

	@Override
	public int size() {
		return behaviors.size();
	}

	@Override
	public boolean isEmpty() {
		return behaviors.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return behaviors.contains(o);
	}

	@Override
	public Object[] toArray() {
		return behaviors.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return behaviors.toArray(a);
	}

	@Override
	public boolean add(Behavior e) {
		if(isIterating()) {
			if(additions == null) additions = new ArrayList<Behavior>();
			return additions.add(e);
		} else {
			return behaviors.add(e);
		}
	}

	@Override
	public boolean remove(Object o) {
		if(isIterating()) {
			if(removals == null) removals = new ArrayList<Object>();
			return removals.add(o);
		} else {
			return behaviors.remove(o);
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return behaviors.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Behavior> c) {
		if(isIterating()) {
			for(Behavior o: c) add(o);
			return true;
		} else {
			return behaviors.addAll(c);
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(isIterating()) {
			for(Object o: c) remove(o);
			return true;
		} else {
			return behaviors.removeAll(c);
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(isIterating()) {
			for(Behavior beh: behaviors) {
				if(!c.contains(beh)) remove(beh);
			}
			return true;
		} else {
			return behaviors.retainAll(c);
		}
	}

	@Override
	public void clear() {
		if(isIterating()) {
			for(Behavior beh: behaviors) remove(beh);
		} else {
			behaviors.clear();
		}		
	}

	@Override
	public void addBehavior(Behavior behavior) {
		add(behavior);
	}

	@Override
	public void removeBehavior(Behavior behavior) {
		remove(behavior);
	}

	@Override
	public Collection<Behavior> getBehaviors() {
		return this;
	}

	@Override
	public void handleEvent(String eventType, Object arg1, Object arg2) {
		for(Behavior beh: this) {
			beh.handleEvent(eventType, this, arg1, arg2);
		}
	}
}
