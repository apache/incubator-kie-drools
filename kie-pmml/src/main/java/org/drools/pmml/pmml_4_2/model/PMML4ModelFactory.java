/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.pmml.pmml_4_2.model;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.MiningModel;
import org.dmg.pmml.pmml_4_2.descr.RegressionModel;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.dmg.pmml.pmml_4_2.descr.Segment;
import org.dmg.pmml.pmml_4_2.descr.TreeModel;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.drools.pmml.pmml_4_2.model.mining.MiningSegmentation;

public class PMML4ModelFactory {
    static PMML4ModelFactory instance = new PMML4ModelFactory();

    private PMML4ModelFactory() {
        // Nothing to do for now
    }

    public static PMML4ModelFactory getInstance() {
        return PMML4ModelFactory.instance;
    }
    
    public PMML4Model getModel(Segment segment, MiningSegmentation segmentation) {
    	PMML4Model model = null;
    	if (segment.getMiningModel() != null) {
    		MiningModel mm = segment.getMiningModel();
    		model = new Miningmodel(mm.getModelName(), mm, segmentation.getOwner() ,null);
    	} else if (segment.getRegressionModel() != null) {
    		RegressionModel rm = segment.getRegressionModel();
    		model = new Regression(rm.getModelName(), rm, segmentation.getOwner(), null);
    	} else if (segment.getScorecard() != null) {
    		Scorecard sc = segment.getScorecard();
    		model = new ScorecardModel(sc.getModelName(), sc, segmentation.getOwner(), null);
    	} else if (segment.getTreeModel() != null) {
    		TreeModel tm = segment.getTreeModel();
    		model = new Treemodel(tm.getModelName(), tm, segmentation.getOwner(), null);
    	}
    	
    	return model;
    }

    public List<PMML4Model> getModels(PMML4Unit owner) {
        List<PMML4Model> pmml4Models = new ArrayList<>();
        owner.getRawPMML().getAssociationModelsAndBaselineModelsAndClusteringModels()
                .forEach(serializable -> {
                    if (serializable instanceof Scorecard) {
                        Scorecard sc = (Scorecard)serializable;
                        ScorecardModel model = new ScorecardModel(sc.getModelName(), sc, null, owner);
                        pmml4Models.add(model);
                    } else if (serializable instanceof RegressionModel) {
                        RegressionModel rm = (RegressionModel)serializable;
                        Regression model = new Regression(rm.getModelName(), rm, null, owner);
                        pmml4Models.add(model);
                    } else if (serializable instanceof TreeModel) {
                    	TreeModel tm = (TreeModel)serializable;
                    	Treemodel model = new Treemodel(tm.getModelName(), tm, null, owner);
                    	pmml4Models.add(model);
                    } else if (serializable instanceof MiningModel) {
                    	MiningModel mm = (MiningModel)serializable;
                    	Miningmodel model = new Miningmodel(mm.getModelName(), mm, null, owner);
                    	pmml4Models.add(model);
                    }
                });
        return pmml4Models;
    }
}
