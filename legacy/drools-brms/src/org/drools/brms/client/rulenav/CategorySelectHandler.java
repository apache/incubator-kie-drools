package org.drools.brms.client.rulenav;

/**
 * This represents an event of a category being selected.
 * @author Michael Neale
 *
 */
public interface CategorySelectHandler {

    /**
     * When a category is selected.
     */
    public void selected(String selectedPath);
    
}
