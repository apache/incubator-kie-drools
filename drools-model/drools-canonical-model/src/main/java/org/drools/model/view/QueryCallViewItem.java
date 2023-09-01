package org.drools.model.view;

import org.drools.model.Argument;
import org.drools.model.QueryDef;

public interface QueryCallViewItem extends ViewItem {

    QueryDef getQuery();

    Argument<?>[] getArguments();

    boolean isOpen();
}
