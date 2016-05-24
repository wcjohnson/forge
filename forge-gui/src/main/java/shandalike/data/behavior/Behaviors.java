package shandalike.data.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Collection of Behaviors that can be safely mutated during iteration. Iterators may see
 * inconsistent views of the collection.
 * Note: this is not a true concurrent collection; don't even think about using it on multiple threads.
 * @author wcj
 */
public class Behaviors implements Iterable<Behavior>, Collection<Behavior>, IBehavioral {
	private class BehIterator implements Iterator<Behavior> {
		int pos = 0;
		
		public BehIterator() {
		}
		
		@Override
		public boolean hasNext() {
			return (pos < behaviors.size());
		}

		@Override
		public Behavior next() {
			pos++;
			return behaviors.get(pos - 1);
		}
		
	}
	
	List<Behavior> behaviors = new ArrayList<Behavior>();
	WeakHashMap<BehIterator,Boolean> outstandingIterators = new WeakHashMap<>();
		
	private boolean isIterating() { return (outstandingIterators.size() > 0); }

	@Override
	public Iterator<Behavior> iterator() {
		BehIterator it = new BehIterator();
		outstandingIterators.put(it, true);
		return it;
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
//		if(isIterating()) {
//			if(additions == null) additions = new ArrayList<Behavior>();
//			return additions.add(e);
//		} else {
//			return behaviors.add(e);
//		}
		return behaviors.add(e);
	}

	@Override
	public boolean remove(Object o) {
		if(isIterating()) {
			// Locate index of object
			int idx = behaviors.indexOf(o);
			if(idx == -1) return false;
			// Adjust outstanding iterators; if removed object was before their position, decr. it.
			for(BehIterator it: outstandingIterators.keySet()) {
				if(idx < it.pos) it.pos--;
			}
			behaviors.remove(idx);
			return true;
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
		behavior.handleEvent("behaviorDidAdd", this, null, null);
	}

	@Override
	public void removeBehavior(Behavior behavior) {
		behavior.handleEvent("behaviorWillRemove", this, null, null);
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
