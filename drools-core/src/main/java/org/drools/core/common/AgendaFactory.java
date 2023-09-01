package org.drools.core.common;


public interface AgendaFactory {

    InternalAgenda createAgenda(InternalWorkingMemory workingMemory);
}
