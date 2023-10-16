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
const inputData = require('./mocks/inputData');

let hit = 0;
let executionId = null;
let cfResults = [];
let baseId = 0;
let searchDomains;
const maxRunningTimeSeconds = 30;

module.exports = (req, res, next) => {
  const _send = res.send;
  res.send = function (body) {
    if (req.path === '/counterfactuals') {
      const query = req.query;
      if (req.method === 'POST') {
        searchDomains = req.body.searchDomains.filter(
          (domain) => domain.value.fixed === false
        );
        try {
          return _send.call(
            this,
            JSON.stringify({
              executionId: query.executionId,
              counterfactualId: faker.random.uuid(),
              maxRunningTimeSeconds: maxRunningTimeSeconds
            })
          );
        } catch (e) {}
      }
      if (req.method === 'GET') {
        if (executionId === null || executionId !== query.executionId) {
          executionId = query.executionId;
          hit = 0;
          cfResults = [];
          baseId = 1000;
        }
        hit++;
        if (hit === 1) {
          for (let i = 0; i < 10; i++) {
            cfResults.unshift(
              getResult(query.executionId, baseId, searchDomains, false)
            );
            baseId++;
          }
        }
        if (hit === 2) {
          for (let i = 0; i < 15; i++) {
            cfResults.unshift(
              getResult(query.executionId, baseId, searchDomains, false)
            );
            baseId++;
          }
          cfResults.splice(15 - cfResults.length);
        }
        if (hit === 3) {
          for (let i = 0; i < 8; i++) {
            cfResults.unshift(
              getResult(query.executionId, baseId, searchDomains, false)
            );
            baseId++;
          }
          cfResults.unshift(
            getResult(query.executionId, baseId, searchDomains, true)
          );
          cfResults.splice(15 - cfResults.length);
          executionId = null;
        }
        try {
          const json = JSON.parse(body);
          return _send.call(
            this,
            JSON.stringify({
              ...json,
              executionId: query.executionId,
              counterfactualId: query.counterfactualId,
              maxRunningTimeSeconds: maxRunningTimeSeconds,
              solutions: cfResults
            })
          );
        } catch (e) {}
      }
    }
    return _send.call(this, body);
  };
  next();
};

function getResult(executionId, baseId, searchDomains, isFinal) {
  return {
    ...interim,
    executionId,
    solutionId: (baseId + 1).toString(),
    stage: isFinal ? 'FINAL' : 'INTERMEDIATE',
    inputs: inputData
      .find((data) => data.executionId === executionId)
      .inputs.map((input) => {
        if (
          searchDomains.filter((domain) => domain.name === input.name).length >
          0
        ) {
          return {
            ...input,
            value: {
              ...input.value,
              value: getChangedValue(input)
            }
          };
        }
        return input;
      })
  };
}

function getChangedValue(input) {
  switch (input.value.type) {
    case 'number':
      return getRandomNumber(input.value.value);
    case 'boolean':
      return !input.value.value;
  }
}
function getRandomNumber(originalValue) {
  return Math.floor(
    Math.random() * originalValue * 0.2 * (Math.random() > 0.5 ? 1 : -1) +
      originalValue
  );
}

const interim = {
  type: 'counterfactual',
  valid: true,
  executionId: 'executionId',
  status: 'SUCCEEDED',
  statusDetails: '',
  counterfactualId: 'counterfactualId',
  solutionId: 'solution1',
  isValid: true,
  stage: 'INTERMEDIATE',
  inputs: [],
  outputs: []
};
