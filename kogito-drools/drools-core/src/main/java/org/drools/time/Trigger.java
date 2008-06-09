package org.drools.time;

import java.io.Externalizable;
import java.util.Date;

public interface Trigger {
    public Date getNextFireTime();
}
