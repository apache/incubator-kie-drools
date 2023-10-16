/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
const faker = require('faker');

const saliencies = {
  status: 'SUCCEEDED',
  statusDetail: '',
  saliencies: []
};
const outcomes = [
  '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
  '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
  '_3QDC8C35-4EB3-451E-874C-DB27A5D6T8Q2',
  '_6O8O6B35-4EB3-451E-874C-DB27A5C5V6B7',
  '_c6e56793-68d0-4683-b34b-5e9d69e7d0d4',
  '_859bea4f-dfc4-480e-96f2-1a756d54b84b',
  '_d361c79e-8c06-4504-bdb2-d6b90b915166',
  '_ff34378e-fe90-4c58-9f7f-b9ce5767a415',
  '_1CFF8C35-4EB2-351E-874C-DB27A2A424C0',
  '_11145678-9012-3456-7890-123456789012',
  '_12345678-9012-3456-7890-123456789012'
];
const features = [
  'Monthly Tax Payment',
  'Monthly Insurance Payment',
  'Monthly HOA Payment',
  'Credit Score',
  'Down Payment',
  'Employment Income',
  'Other Income',
  'Assets',
  'Liabilities',
  'Lender Ratings',
  'Other Income 2',
  'Assets 2',
  'Liabilities 2',
  'Lender Ratings 2'
];

for (let i = features.length - 1; i >= 0; i--) {
  const j = Math.floor(Math.random() * i);
  const temp = features[i];
  features[i] = features[j];
  features[j] = temp;
}

outcomes.forEach((item) => {
  const outcome = {
    outcomeId: item,
    featureImportance: []
  };
  // simulating an outcome with few features scores that doesn't need
  // the "complete chart" dialog. The targeted outcome is "Risk Score" from
  // the decision with the so called twoSimpleOutcomes
  // ("Mortgage approval" and "Risk score")
  const featuresCount =
    item === '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0' ? 4 : features.length - 1;
  for (let i = 0; i <= featuresCount; i++) {
    const feature = {
      featureName: features[i],
      featureId: faker.random.uuid(),
      featureScore: Math.random() * (Math.random() > 0.5 ? 1 : -1)
    };
    outcome.featureImportance.push(feature);
  }
  // simulating an outcome with no explanation info
  if (item === '_ff34378e-fe90-4c58-9f7f-b9ce5767a415') {
    outcome.featureImportance = [];
  }
  saliencies.saliencies.push(outcome);
});

module.exports = saliencies;
