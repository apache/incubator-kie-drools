const faker = require('faker');
//const inputDataWithScores = require('./mocks/inputDataWithScores');
const inputData = require('./mocks/inputData');
const outcomeData = require('./mocks/outcomes');
const outcomeDetailData = require('./mocks/outcomeDetail');
const modelData = require('./mocks/modelData');
const featureImportance = require('./mocks/featureImportance');
const executionIdBase = require('./mocks/executionIdBase');

let generateFakeAPIs = () => {
  let decisionsList = [];

  decisionsList.push({
    executionId: executionIdBase + 1000,
    executionDate: faker.date.recent(),
    executionType: 'DECISION',
    executedModelName: 'fraud-score',
    executionSucceeded: true,
    executorName: 'Technical User'
  });

  for (let id = 1001; id < 1010; id++) {
    let executionDate = faker.date.past();

    decisionsList.push({
      executionId: executionIdBase + id,
      executionDate: executionDate,
      executionType: 'DECISION',
      executedModelName: 'fraud-score',
      executionSucceeded: true,
      executorName: 'Technical User'
    });
  }

  let executionsList = {
    total: 65,
    limit: 10,
    offset: 0,
    headers: decisionsList
  };

  return {
    executions: executionsList,
    decisions: decisionsList,
    inputs: inputData,
    outcomes: outcomeData,
    outcomeDetail: outcomeDetailData,
    models: modelData,
    featureImportance: featureImportance
  };
};

module.exports = generateFakeAPIs;
