package org.kie.scanner;

import org.kie.api.builder.KieScanner;
import org.kie.api.builder.KieScannerFactoryService;

public class KieScannerFactoryServiceImpl implements KieScannerFactoryService {
    @Override
    public KieScanner newKieScanner() {
        return new KieRepositoryScannerImpl();
    }
}
