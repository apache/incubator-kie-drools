package org.drools.model.functions;

import java.io.Serializable;

public interface BlockN extends Serializable {
    void execute(Object... objs);
}
