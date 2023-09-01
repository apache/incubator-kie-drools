package org.drools.mvel.accessors;

import java.time.ZonedDateTime;
import java.util.Date;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;

public class BaseZonedDateTimeClassFieldReader extends BaseDateClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseZonedDateTimeClassFieldReader() {

    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     *
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseZonedDateTimeClassFieldReader( final int index,
                                                 final Class fieldType,
                                                 final ValueType valueType ) {
        super( index,
               fieldType,
               valueType );
    }

    protected Date getDate(ValueResolver valueResolver, Object object) {
        ZonedDateTime zdt = ((ZonedDateTime)getValue( valueResolver, object ));
        return Date.from( zdt.toInstant() );
    }
}
