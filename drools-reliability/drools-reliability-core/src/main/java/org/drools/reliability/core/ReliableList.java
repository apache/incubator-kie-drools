package org.drools.reliability.core;

import org.drools.core.phreak.PropagationList;

public interface ReliableList extends PropagationList {
    void safepoint();
}
