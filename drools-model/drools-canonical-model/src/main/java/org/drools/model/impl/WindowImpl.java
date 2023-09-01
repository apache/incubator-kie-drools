package org.drools.model.impl;

import java.util.concurrent.TimeUnit;

import org.drools.model.Window;

public class WindowImpl extends AbstractWindow implements Window {

    public WindowImpl( Type type, long value ) {
        super(type, value);
    }

    public WindowImpl( Type type, long value, TimeUnit timeUnit ) {
        super(type, value, timeUnit);
    }
}
