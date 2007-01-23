package org.drools;

import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;
import org.drools.spi.RuleFlowGroup;

public interface Agenda {

    public WorkingMemory getWorkingMemory();

    public org.drools.util.LinkedList getScheduledItems();

    public boolean setFocus(AgendaGroup agendaGroup);

    public void setFocus(String name);

    public AgendaGroup getFocus();

    public AgendaGroup getAgendaGroup(String name);
    
    public RuleFlowGroup getRuleFlowGroup(String name);

    public AgendaGroup[] getAgendaGroups();

    public AgendaGroup[] getStack();

    public ActivationGroup getActivationGroup(String name);

    /**
     * Iterates all the <code>AgendGroup<code>s in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of <code>Activation</code>s on the focus stack
     */
    public int focusStackSize();

    /**
     * Iterates all the modules in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of activations on the focus stack
     */
    public int agendaSize();

    public Activation[] getActivations();

    public Activation[] getScheduledActivations();

    /**
     * Clears all Activations from the Agenda
     * 
     */
    public void clearAgenda();

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     * 
     * @param agendaGroup
     */
    public void clearAgendaGroup(String name);

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     * 
     * @param agendaGroup
     */
    public void clearAgendaGroup(AgendaGroup agendaGroup);

    /**
     * Clears all Activations from an Activation-Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     * 
     * @param activationGroup
     */
    public void clearActivationGroup(String name);

    /**
     * Clears all Activations from an Xor Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     * 
     * @param activationGroup
     */
    public void clearActivationGroup(ActivationGroup activationGroup);

}