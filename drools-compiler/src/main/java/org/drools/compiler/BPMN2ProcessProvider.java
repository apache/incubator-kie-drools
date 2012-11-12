package org.drools.compiler;

import org.kie.Service;

public interface BPMN2ProcessProvider extends Service {

    void configurePackageBuilder(PackageBuilder packageBuilder);

}
