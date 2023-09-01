package org.drools.commands.fluent;

import java.util.List;

import org.drools.commands.impl.NotTransactionalCommand;
import org.drools.commands.runtime.DisposeCommand;
import org.kie.api.runtime.Executable;

public interface InternalExecutable extends Executable {

    List<Batch> getBatches();

    default boolean canRunInTransaction() {
        return getBatches().stream()
                .flatMap(batch -> batch.getCommands().stream())
                .noneMatch(NotTransactionalCommand.class::isInstance);
    }

    default boolean requiresDispose() {
        return getBatches().stream()
                .flatMap(batch -> batch.getCommands().stream())
                .anyMatch(DisposeCommand.class::isInstance);
    }
}
