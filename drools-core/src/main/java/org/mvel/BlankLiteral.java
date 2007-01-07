package org.mvel;

import static org.mvel.util.PropertyTools.isNumeric;

import static java.lang.String.valueOf;
import java.util.Collection;

public class BlankLiteral {
    public static final BlankLiteral INSTANCE = new BlankLiteral();

    public boolean equals(Object obj) {
        if (obj == null || "".equals(valueOf(obj))) {
            return true;
        }
        else if (isNumeric(obj)) {
            return "0".equals(valueOf(obj));
        }
        else if (obj instanceof Collection) {
            return ((Collection) obj).size() == 0;
        }
        else if (obj.getClass().isArray()) {
            return ((Object[]) obj).length == 0;
        }
        return false;
    }
}
