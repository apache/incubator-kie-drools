package org.kie.internal.builder;

import org.kie.api.builder.Message;

public interface InternalMessage extends Message {

    String getKieBaseName();

    InternalMessage setKieBaseName(String kieBaseName);

}
