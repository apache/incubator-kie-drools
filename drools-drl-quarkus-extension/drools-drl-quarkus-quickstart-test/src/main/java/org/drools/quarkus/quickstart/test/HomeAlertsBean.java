package org.drools.quarkus.quickstart.test;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.quarkus.quickstart.test.model.Alert;
import org.drools.quarkus.quickstart.test.model.CCTV;
import org.drools.quarkus.quickstart.test.model.Light;
import org.drools.quarkus.quickstart.test.model.Smartphone;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;

@ApplicationScoped
public class HomeAlertsBean {
	
    @Inject
    RuleUnit<HomeRuleUnitData> ruleUnit;
    
    public Collection<Alert> computeAlerts(Collection<Light> lights, Collection<CCTV> cameras, Collection<Smartphone> phones) {
        HomeRuleUnitData homeUnitData = new HomeRuleUnitData();
        lights.forEach(homeUnitData.getLights()::add);
        cameras.forEach(homeUnitData.getCctvs()::add);
        phones.forEach(homeUnitData.getSmartphones()::add);

        try (final RuleUnitInstance<HomeRuleUnitData> unitInstance = ruleUnit.createInstance(homeUnitData)) {
            var queryResults = unitInstance.executeQuery("All Alerts");
            return queryResults.toList().stream()
                    .flatMap(m -> m.values().stream()
                            .filter(Alert.class::isInstance)
                            .map(Alert.class::cast))
                    .collect(Collectors.toList());
        }
    }
}
