package org.optaplanner.examples.projectjobscheduling.domain.resource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PjsGlobalResource")
public class GlobalResource extends Resource {

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public boolean isRenewable() {
        return true;
    }

}
