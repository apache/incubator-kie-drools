package org.drools.core.base;

public class UndefinedCalendarExcption extends RuntimeException {

    public UndefinedCalendarExcption(String name) {
        super("No calendar named '" + name + "' has been defined on this session");
    }
}
