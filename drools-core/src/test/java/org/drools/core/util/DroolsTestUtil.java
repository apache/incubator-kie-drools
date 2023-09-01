package org.drools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.InternalRuleBase;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;

public class DroolsTestUtil {
    public static Map<String, Rule> rulestoMap( Collection<Rule> rules ) {
        Map<String, Rule> ret = new HashMap<>();
        for ( Rule rule : rules ) {
            ret.put( rule.getName(), rule );
        }
        return ret;
    }

    public static Map<String, Rule> rulestoMap( KieBase kbase ) {
        List<Rule> rules = new ArrayList();
        for ( KiePackage pkg : ((InternalRuleBase)kbase).getPackages() ) {
            rules.addAll(pkg.getRules());
        }

        return rulestoMap( rules );
    }

}
