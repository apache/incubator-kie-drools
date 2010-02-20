package org.drools.compiler;

import org.drools.Service;

public interface BPMN2ProcessProvider extends Service {

    void configurePackageBuilder(PackageBuilder packageBuilder);

}
