package org.drools.verifier.core.relations;

public interface HumanReadable {

    String toHumanReadableString();

    static String toHumanReadableString(final Object object) {
        if (object == null) {
            return "";
        } else if (object instanceof HumanReadable) {
            return ((HumanReadable) object).toHumanReadableString();
        } else {
            return object.toString();
        }
    }
}
