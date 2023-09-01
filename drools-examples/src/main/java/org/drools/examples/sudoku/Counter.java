package org.drools.examples.sudoku;

/**
 * A counter bean.
 */
public class Counter {

    private int count;
    
    /**
     * Constructor, setting an initial value.
     * @param init the initialization value
     */
    public Counter( int init ){
        this.count = init;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
