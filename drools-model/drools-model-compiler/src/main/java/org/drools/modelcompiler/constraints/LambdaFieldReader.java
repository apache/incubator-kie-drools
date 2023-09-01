package org.drools.modelcompiler.constraints;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.base.base.CoreComponentsBuilder;
import org.drools.model.functions.Function1;
import org.drools.util.ClassUtils;

import static org.drools.base.util.Drools.hasMvel;

public class LambdaFieldReader implements Function1 {

    private final Method accessor;
    private final String field;

    public LambdaFieldReader( Class<?> clazz, String field ) {
        this.accessor = ClassUtils.getAccessor( clazz, field );
        this.field = field;
    }

    @Override
    public Object apply( Object o ) {
        try {
            if (accessor != null) {
                return accessor.invoke( o );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException( e );
        }
        if (!hasMvel()) {
            throw new RuntimeException("Complex timestamp expressions can be used only with drools-mvel on classpath");
        }
        return CoreComponentsBuilder.get().getMVELExecutor().eval( field, o );
    }
}
