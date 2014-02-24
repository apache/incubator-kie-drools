package org.kie.api.builder;

import org.kie.api.Service;

public interface KieScannerFactoryService extends Service {

    KieScanner newKieScanner();
}
