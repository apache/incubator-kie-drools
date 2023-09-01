package org.drools.model;

public interface Query extends NamedModelItem {

    View getView();

    Variable<?>[] getArguments();
}
