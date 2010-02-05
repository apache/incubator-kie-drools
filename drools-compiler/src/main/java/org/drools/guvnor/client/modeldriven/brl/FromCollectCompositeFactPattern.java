
package org.drools.guvnor.client.modeldriven.brl;

/**
 *
 * @author esteban
 */
public class FromCollectCompositeFactPattern extends FromCompositeFactPattern {

    private IPattern rightPattern;

    public FromCollectCompositeFactPattern() {
    }

    public IPattern getRightPattern() {
        return rightPattern;
    }

    public void setRightPattern(IPattern rightPattern) {
        this.rightPattern = rightPattern;
    }

}
