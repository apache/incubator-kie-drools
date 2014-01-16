package org.drools.core.command;

import org.kie.api.runtime.rule.EntryPoint;

public interface EntryPointCreator {
    EntryPoint getEntryPoint(String entryPoint);
}
