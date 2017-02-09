package util;

import java.util.Vector;

public class IObservable<T> {
    private boolean changed = false;
    private Vector<IObserver<T>> obs;
    
    public IObservable() {
        obs = new Vector<>();
    }
    public synchronized void addObserver(IObserver<T> o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
            o.update((T)this);
        }
    }

    public synchronized void deleteObserver(IObserver<T> o) {
        obs.removeElement(o);
    }

    public void notifyObservers() {
        Object[] arrLocal;

        synchronized (this) {
            /* We don't want the Observer doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each Observable from
             * the Vector and store the state of the Observer
             * needs synchronization, but notifying observers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added Observer will miss a
             *   notification in progress
             * 2) a recently unregistered Observer will be
             *   wrongly notified when it doesn't care
             */
            if (!changed)
                return;
            arrLocal = obs.toArray();
            clearChanged();
        }

        for (int i = arrLocal.length-1; i>=0; i--) {
        	if(obs.contains(arrLocal[i])) {
        		((IObserver<T>)arrLocal[i]).update((T)this);
        	}
        }
    }

    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    protected synchronized void setChanged() {
        changed = true;
    }
    protected synchronized void clearChanged() {
        changed = false;
    }

    public synchronized boolean hasChanged() {
        return changed;
    }

    public synchronized int countObservers() {
        return obs.size();
    }
}
