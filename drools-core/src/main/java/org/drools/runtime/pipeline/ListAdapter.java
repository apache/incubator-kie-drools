package org.drools.runtime.pipeline;

import java.util.List;

public interface ListAdapter extends Receiver, Stage{
    
    List< Object > getList();

    void setList(List< Object > list);
    
}
