package org.kie.api.command;

import org.kie.api.runtime.Context;

public interface ExecutableCommand<T> extends Command<T> {

    T execute(Context context);

    default boolean autoFireAllRules() {
        return true;
    }
}
