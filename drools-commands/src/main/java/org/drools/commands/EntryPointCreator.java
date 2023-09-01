package org.drools.commands;

import org.kie.api.runtime.rule.EntryPoint;

public interface EntryPointCreator {
    EntryPoint getEntryPoint(String entryPoint);
}
