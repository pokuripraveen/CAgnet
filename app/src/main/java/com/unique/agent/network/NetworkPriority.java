package com.unique.agent.network;

/**
 * Created by praveenpokuri on 21/08/17.
 */

public enum NetworkPriority implements Prioritized {
    /** Value is 100. */
    LOW(100),

    /** Value is 200 */
    DEFAULT(200),

    /** Value is 300 */
    HIGH(300),

    /** Value is 400 */
    HIGHEST(400);

    private final int value;

    NetworkPriority(int value) {
        this.value = value;
    }

    public int getPriority() {
        return value;
    }
}

