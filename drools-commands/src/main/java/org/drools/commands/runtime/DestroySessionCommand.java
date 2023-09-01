package org.drools.commands.runtime;

import org.drools.commands.SingleSessionCommandService;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;

public class DestroySessionCommand extends DisposeCommand {

    private ExecutableRunner runner;

    public DestroySessionCommand() {

    }

    public DestroySessionCommand(ExecutableRunner runner ) {
        this.runner = runner;
    }

    public Void execute(Context context) {
        if (runner != null && runner instanceof SingleSessionCommandService) {
           ((SingleSessionCommandService) runner).destroy();
        }
        super.execute(context);
        return null;
    }

    public String toString() {
        return "Destroy session command";
    }
}
