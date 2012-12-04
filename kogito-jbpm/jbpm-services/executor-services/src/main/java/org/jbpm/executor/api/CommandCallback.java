package org.jbpm.executor.api;

public interface CommandCallback {

    void onCommandDone(CommandContext ctx, ExecutionResults results);
}
