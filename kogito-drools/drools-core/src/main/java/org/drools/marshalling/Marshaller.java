package org.drools.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.concurrent.ExecutorService;
import org.drools.runtime.Environment;

public interface Marshaller {

    public StatefulSession read(final InputStream stream,
                                final InternalRuleBase ruleBase,
                                final int id,
                                final ExecutorService executor,
                                final SessionConfiguration config,
                                final Environment environment) throws IOException,
                                                               ClassNotFoundException;

    public StatefulSession read(final InputStream stream,
                                final InternalRuleBase ruleBase,
                                final StatefulSession session) throws IOException,
                                                              ClassNotFoundException;

    public abstract void write(final OutputStream stream,
                               final InternalRuleBase ruleBase,
                               final StatefulSession session) throws IOException;

}