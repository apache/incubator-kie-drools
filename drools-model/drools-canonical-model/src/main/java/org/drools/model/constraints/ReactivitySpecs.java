package org.drools.model.constraints;

import org.drools.model.BitMask;
import org.drools.model.DomainClassMetadata;
import org.drools.model.bitmask.AllSetButLastBitMask;

public class ReactivitySpecs {

    public static final ReactivitySpecs EMPTY = new ReactivitySpecs();

    private final BitMask bitMask;
    private final String[] props;

    private ReactivitySpecs() {
        this.props = new String[0];
        this.bitMask = AllSetButLastBitMask.get();
    }

    public ReactivitySpecs( DomainClassMetadata metadata, String... props ) {
        this.props = props;
        this.bitMask = metadata != null ? BitMask.getPatternMask(metadata, props) : null;
    }

    public BitMask getBitMask() {
        return bitMask;
    }

    public String[] getProps() {
        return props;
    }
}
