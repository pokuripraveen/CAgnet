package com.unique.agent.concurrent;

import com.unique.agent.network.Prioritized;

/**
 * Created by praveenpokuri on 21/08/17.
 */

class PrioritizedComparator<T> implements java.util.Comparator<T> {
    @Override
    public int compare(T t1, T t2) {
        if (t1 instanceof Prioritized && t2 instanceof Prioritized) {
            Prioritized a = (Prioritized) t1;
            Prioritized b = (Prioritized) t2;
            return Integer.valueOf(b.getPriority()).compareTo(a.getPriority());
        }
        return 0;
    }
}
