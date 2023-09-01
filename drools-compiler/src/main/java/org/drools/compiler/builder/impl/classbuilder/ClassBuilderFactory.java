package org.drools.compiler.builder.impl.classbuilder;

import org.drools.base.rule.TypeDeclaration;
import org.kie.api.internal.utils.KieService;

import static org.drools.base.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

public interface ClassBuilderFactory extends KieService {

    class Holder {
        private static final ClassBuilderFactory factory = getFactory();

        private static ClassBuilderFactory getFactory() {
            ClassBuilderFactory instance = KieService.load( ClassBuilderFactory.class );
            return instance != null ? instance : throwExceptionForMissingMvel();
        }
    }

    static ClassBuilderFactory get() {
        return Holder.factory;
    }

    ClassBuilder getBeanClassBuilder();

    EnumClassBuilder getEnumClassBuilder();

    ClassBuilder getPropertyWrapperBuilder();

    void setPropertyWrapperBuilder(ClassBuilder pcb);

    ClassBuilder getClassBuilder(TypeDeclaration type);
}
