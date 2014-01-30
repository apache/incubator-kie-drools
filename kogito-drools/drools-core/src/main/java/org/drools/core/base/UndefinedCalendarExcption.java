package org.drools.core.base;

import org.drools.core.RuntimeDroolsException;

public class UndefinedCalendarExcption extends RuntimeDroolsException {

    public UndefinedCalendarExcption(String name) {
        super("No calendar named '" + name + "' has been defined on this session");
    }
}
