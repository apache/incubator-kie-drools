package org.drools.scenariosimulation.api.model;

/**
 * Tuple with <code>BackgroundData</code>> and its index
 */
public class BackgroundDataWithIndex extends ScesimDataWithIndex<BackgroundData> {
    
    public BackgroundDataWithIndex() {
        // CDI
    }

    public BackgroundDataWithIndex(int index, BackgroundData backgroundData) {
        super(index, backgroundData);
    }

}