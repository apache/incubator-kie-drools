package org.drools.core.util.debug;

import java.util.Comparator;

public class LeftMemorySizeComparator implements Comparator<NodeInfo>{

    public int compare(NodeInfo o1,
                       NodeInfo o2) {
        return (int) ( o2.getTupleMemorySize() - o1.getTupleMemorySize()) ;
    }

}
