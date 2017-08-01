/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.demo.insurance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculateClaimService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculateClaimService.class);

    public ClaimReport calculate(ClaimReport claimReport) {
        Double currentAmount = claimReport.getAmount();       
        if (currentAmount == null) {
            currentAmount = new Double(10);
        } else {
            currentAmount += 5;
        }
        logger.info("Calculated amount on claim {} is {}", claimReport, currentAmount);
        claimReport.setAmount(currentAmount);
        claimReport.setCalculated(Boolean.TRUE);
        
        return claimReport;
    }
}
