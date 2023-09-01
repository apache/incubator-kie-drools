package org.drools.template.model;

/**
 * Classes that implement this interface should generate DRL fragments according
 * to the drools java semantic module.
 */
public interface DRLJavaEmitter {

    /**
     * Each node can add its contribution to the output
     */
    void renderDRL(DRLOutput out);

}
