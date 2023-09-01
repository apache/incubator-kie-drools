package org.drools.verifier.core.configuration;

import java.util.Date;

public interface DateTimeFormatProvider {

    String format(final Date dateValue);

    Date parse(final String dateValue);
}
