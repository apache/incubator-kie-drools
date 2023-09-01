package org.kie.internal.command;

import org.kie.internal.process.CorrelationKey;

public interface CorrelationKeyCommand {

    public void setCorrelationKey(CorrelationKey key);

    public CorrelationKey getCorrelationKey();

}
