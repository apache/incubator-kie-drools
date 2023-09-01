package org.drools.quarkus.quickstart.test;

import org.drools.quarkus.quickstart.test.model.Alert;
import org.drools.quarkus.quickstart.test.model.CCTV;
import org.drools.quarkus.quickstart.test.model.Light;
import org.drools.quarkus.quickstart.test.model.Smartphone;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class HomeRuleUnitData implements RuleUnitData {
    
    private final DataStore<Light> lights;
    private final DataStore<CCTV> cctvs;
    private final DataStore<Smartphone> smartphones;

    private final DataStore<Alert> alerts = DataSource.createStore();

    public HomeRuleUnitData() {
        this(DataSource.createStore(), DataSource.createStore(), DataSource.createStore());
    }

    public HomeRuleUnitData(DataStore<Light> lights, DataStore<CCTV> cctvs, DataStore<Smartphone> smartphones) {
		this.lights = lights;
		this.cctvs = cctvs;
		this.smartphones = smartphones;
	}

	public DataStore<Light> getLights() {
		return lights;
	}

	public DataStore<CCTV> getCctvs() {
		return cctvs;
	}

	public DataStore<Smartphone> getSmartphones() {
		return smartphones;
	}

	public DataStore<Alert> getAlerts() {
		return alerts;
	}
}
