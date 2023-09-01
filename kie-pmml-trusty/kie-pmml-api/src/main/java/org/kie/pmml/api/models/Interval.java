package org.kie.pmml.api.models;

import java.io.Serializable;

/**
 * User-friendly representation of an <b>Interval</b>
 */
public class Interval implements Serializable {

    private static final long serialVersionUID = -5245266051098683475L;
    private final Number leftMargin;
    private final Number rightMargin;

    public Interval(Number leftMargin, Number rightMargin) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
    }

    public Number getLeftMargin() {
        return leftMargin;
    }

    public Number getRightMargin() {
        return rightMargin;
    }
}
