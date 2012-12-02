package org.drools.cdi;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class CDITestRunner extends BlockJUnit4ClassRunner {
    
    // Weld is slow to startup, due to scanning, so make static singleton
    private static final WeldContainer weld;    
    static {
        weld = new Weld().initialize();
    }
    
    public CDITestRunner(Class cls) throws InitializationError {
        super(cls);
    }
    
    @Override
    protected Object createTest() throws Exception {
        return weld.instance().select( getTestClass().getJavaClass() ).get();
    }

}