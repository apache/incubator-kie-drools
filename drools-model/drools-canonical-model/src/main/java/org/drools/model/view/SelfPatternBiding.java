package org.drools.model.view;

import org.drools.model.Variable;

public class SelfPatternBiding<T> extends BindViewItem1<T> {

    public SelfPatternBiding( Variable<T> patternVariable ) {
        super( patternVariable, x -> x, patternVariable, new String[0], new String[0] );
    }
}
