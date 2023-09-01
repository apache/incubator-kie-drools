package org.kie.internal.builder;

import java.util.List;

import com.sun.tools.xjc.Options;
import org.kie.api.io.ResourceConfiguration;

public interface JaxbConfiguration
    extends
    ResourceConfiguration {

    public Options getXjcOpts();


    public String getSystemId();


    public List<String> getClasses();

}

