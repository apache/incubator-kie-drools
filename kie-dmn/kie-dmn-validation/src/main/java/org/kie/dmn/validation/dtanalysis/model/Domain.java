package org.kie.dmn.validation.dtanalysis.model;

import java.util.List;

public interface Domain {

    Bound<?> getMin();

    Bound<?> getMax();

    Interval getDomainMinMax();

    List getDiscreteValues();

    boolean isDiscreteDomain();

}
