/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.explainability.integrationtests.dmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.utils.DataUtils;

public class DmnTestUtils {

    public static List<PredictionInput> randomFraudScoringInputs() {
        List<Map<String, Object>> transactions = new ArrayList<>();
        Map<String, Object> t1 = new HashMap<>();
        t1.put("Card Type", "Debit");
        t1.put("Location", "Local");
        t1.put("Amount", 1000);
        t1.put("Auth Code", "Authorized");
        transactions.add(t1);
        Map<String, Object> t2 = new HashMap<>();
        t2.put("Card Type", "Prepaid");
        t2.put("Location", "Local");
        t2.put("Amount", 100000);
        t2.put("Auth Code", "Denied");
        transactions.add(t2);
        Map<String, Object> map = new HashMap<>();
        map.put("Transactions", transactions);
        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", map));
        PredictionInput predictionInput = new PredictionInput(features);

        return getPredictionInputs(predictionInput);
    }

    public static List<PredictionInput> randomLoanEligibilityInputs() {

        Map<String, Object> client = new HashMap<>();
        client.put("Age", 43);
        client.put("Salary", 1950);
        client.put("Existing payments", 100);
        Map<String, Object> loan = new HashMap<>();
        loan.put("Duration", 15);
        loan.put("Installment", 100);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Client", client);
        contextVariables.put("Loan", loan);
        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        PredictionInput predictionInput = new PredictionInput(features);

        return getPredictionInputs(predictionInput);
    }

    public static List<PredictionInput> randomPrequalificationInputs() {
        final Map<String, Object> borrower = new HashMap<>();
        borrower.put("Monthly Other Debt", 1000);
        borrower.put("Monthly Income", 10000);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Appraised Value", 500000);
        contextVariables.put("Loan Amount", 300000);
        contextVariables.put("Credit Score", 600);
        contextVariables.put("Borrower", borrower);
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        PredictionInput predictionInput = new PredictionInput(features);

        return getPredictionInputs(predictionInput);
    }

    public static List<PredictionInput> randomTrafficViolationInputs() {

        final Map<String, Object> driver = new HashMap<>();
        driver.put("Points", 10);
        final Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 150);
        violation.put("Speed Limit", 130);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Driver", driver);
        contextVariables.put("Violation", violation);
        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        PredictionInput predictionInput = new PredictionInput(features);

        return getPredictionInputs(predictionInput);
    }

    private static List<PredictionInput> getPredictionInputs(PredictionInput predictionInput) {
        List<PredictionInput> predictionInputs = new ArrayList<>();
        Random random = new Random();
        int noOfPerturbations = predictionInput.getFeatures().size();
        PerturbationContext perturbationContext = new PerturbationContext(4L, random, noOfPerturbations);
        for (int i = 0; i < 100; i++) {
            List<Feature> perturbFeatures = DataUtils.perturbFeatures(predictionInput.getFeatures(),
                    perturbationContext);
            predictionInputs.add(new PredictionInput(perturbFeatures));
        }
        return predictionInputs;
    }

}
