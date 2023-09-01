package org.kie.api.runtime.rule;

public interface AgendaGroup {

    /**
     * Static reference to determine the default <code>AgendaGroup</code> name.
     */
    String MAIN = "MAIN";

    /**
     * @return
     *      The AgendaGroup name
     */
    String getName();

    void clear();

    void setFocus();
}
