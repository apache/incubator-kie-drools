package org.drools.kiesession.debug;

import java.util.Comparator;

public class RightMemorySizeComparator implements Comparator<NodeInfo>{

    public int compare(NodeInfo o1,
                       NodeInfo o2) {
        return (int) ( o2.getFactMemorySize() - o1.getFactMemorySize()) ;
    }

}
