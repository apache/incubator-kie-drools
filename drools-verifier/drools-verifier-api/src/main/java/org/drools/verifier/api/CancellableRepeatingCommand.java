package org.drools.verifier.api;

public interface CancellableRepeatingCommand {

    boolean execute();

    void cancel();
}