package org.kie.internal.ruleunit;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.conf.ClockTypeOption;

public interface RuleUnitDescription {

    default Class<?> getRuleUnitClass() {
        return null;
    }

    String getRuleUnitName();

    String getCanonicalName();

    String getSimpleName();

    String getPackageName();

    default String getEntryPointName(String name) {
        return getRuleUnitName() + "." + name;
    }

    Optional<Class<?>> getDatasourceType(String name );

    Optional<Type> getVarType(String name );

    RuleUnitVariable getVar(String name);

    boolean hasVar( String name );

    Collection<String> getUnitVars();

    Collection<? extends RuleUnitVariable> getUnitVarDeclarations();

    boolean hasDataSource( String name );

    ClockTypeOption getClockType();

    Collection<KieBaseOption> getKieBaseOptions();
}
