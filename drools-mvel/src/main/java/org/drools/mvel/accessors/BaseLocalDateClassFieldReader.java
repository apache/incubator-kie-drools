package org.drools.mvel.accessors;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;

public class BaseLocalDateClassFieldReader extends BaseDateClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseLocalDateClassFieldReader() {

    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     *
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseLocalDateClassFieldReader( final int index,
                                             final Class fieldType,
                                             final ValueType valueType ) {
        super( index,
               fieldType,
               valueType );
    }

    protected Date getDate(ValueResolver valueResolver, Object object) {
        LocalDate ld = ((LocalDate )getValue( valueResolver, object ));
        return Date.from( ld.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() );
    }
}
