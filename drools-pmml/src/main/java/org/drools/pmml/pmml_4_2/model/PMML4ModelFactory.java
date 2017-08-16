package org.drools.pmml.pmml_4_2.model;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;

public class PMML4ModelFactory {
    static PMML4ModelFactory instance = new PMML4ModelFactory();

    private PMML4ModelFactory() {
        // Nothing to do for now
    }

    public static PMML4ModelFactory getInstance() {
        return PMML4ModelFactory.instance;
    }

    public List<PMML4Model> getModels(PMML4Unit owner) {
        List<PMML4Model> pmml4Models = new ArrayList<>();
        owner.getRawPMML().getAssociationModelsAndBaselineModelsAndClusteringModels()
                .forEach(serializable -> {
                    if (serializable instanceof Scorecard) {
                        Scorecard sc = (Scorecard)serializable;
                        ScorecardModel model = new ScorecardModel(sc.getModelName(), sc, owner);
                        pmml4Models.add(model);
                    }
                });
        return pmml4Models;
    }
}
