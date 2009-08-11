package org.drools.common;

import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;

public interface InternalAgendaGroup extends AgendaGroup {
    public Activation getNext();
    
    public void add(Activation activation);
    
    public void setActive(boolean activate);
    
    public Activation[] getQueue();
    
    public void clear();

	public void remove(AgendaItem agendaItem);        
}
