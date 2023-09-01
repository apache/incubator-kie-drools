package org.drools.compiler.builder.impl;

import com.sun.tools.xjc.Options;
import org.drools.compiler.builder.conf.JaxbConfigurationImpl;
import org.kie.internal.builder.JaxbConfiguration;
import org.kie.internal.builder.JaxbConfigurationFactoryService;

public class JaxbConfigurationFactoryServiceImpl implements JaxbConfigurationFactoryService {

    @Override
    public JaxbConfiguration newJaxbConfiguration( Options xjcOpts, String systemId) {
        return new JaxbConfigurationImpl( xjcOpts, systemId );
    }
}
