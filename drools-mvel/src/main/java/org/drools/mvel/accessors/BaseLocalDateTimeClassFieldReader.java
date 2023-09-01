package org.drools.mvel.accessors;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;

public class BaseLocalDateTimeClassFieldReader extends BaseDateClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseLocalDateTimeClassFieldReader() {

    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     *
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseLocalDateTimeClassFieldReader(final int index,
                                                final Class fieldType,
                                                final ValueType valueType ) {
        super( index,
               fieldType,
               valueType );
    }

    protected Date getDate(ValueResolver valueResolver, Object object) {
        LocalDateTime ldt = ((LocalDateTime)getValue( valueResolver, object ));
        return Date.from( ldt.atZone( ZoneId.systemDefault() ).toInstant() );
    }
}
