package org.kie.scanner.management;

import java.util.concurrent.atomic.AtomicInteger;

import javax.management.ObjectName;

import org.drools.compiler.kie.builder.impl.InternalKieScanner;

public class KieScannerMBeanImpl implements KieScannerMBean {
    
    private static final String MBEAN_NAME = "org.kie:type=scanner,id=%s,c=%d";
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    private ObjectName mbeanName;
    private InternalKieScanner scanner;
    
    public KieScannerMBeanImpl( InternalKieScanner scanner ) {
        this.scanner = scanner;
        String name = String.format(MBEAN_NAME, ObjectName.quote(scanner.getScannerReleaseId().toExternalForm()), counter.incrementAndGet());
        this.mbeanName = MBeanUtils.createObjectName( name );
        MBeanUtils.registerMBean(this, KieScannerMBean.class, mbeanName);
    }
    
    public ObjectName getMBeanName() {
        return this.mbeanName;
    }
    
    /* (non-Javadoc)
     * @see org.kie.scanner.management.KieScannerMBean#getScannerReleaseId()
     */
    @Override
    public String getScannerReleaseId() {
        return scanner.getScannerReleaseId().toExternalForm();
    }
    
    /* (non-Javadoc)
     * @see org.kie.scanner.management.KieScannerMBean#getCurrentReleaseId()
     */
    @Override
    public String getCurrentReleaseId() {
        return scanner.getCurrentReleaseId().toExternalForm();
    }
    
    /* (non-Javadoc)
     * @see org.kie.scanner.management.KieScannerMBean#getStatus()
     */
    @Override
    public String getStatus() {
        return this.scanner.getStatus().toString();
    }
    
    /* (non-Javadoc)
     * @see org.kie.scanner.management.KieScannerMBean#scanNow()
     */
    @Override
    public void scanNow() {
        this.scanner.scanNow();
    }
    
    /* (non-Javadoc)
     * @see org.kie.scanner.management.KieScannerMBean#start(long)
     */
    @Override
    public void start( long pollingInterval ) {
        this.scanner.start(pollingInterval);
    }
    
    /* (non-Javadoc)
     * @see org.kie.scanner.management.KieScannerMBean#stop()
     */
    @Override
    public void stop() {
        this.scanner.stop();
    }
    

}
