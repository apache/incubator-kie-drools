const faker = require('faker');
const inputData = require('./mocks/inputData');
const outcomeData = require('./mocks/outcomes');
const outcomeDetailData = require('./mocks/outcomeDetail');
const modelData = require('./mocks/modelData');
const salienciesData = require('./mocks/saliencies');
const executionIds = require('./mocks/executionIds');
const cfData = require('./mocks/counterfactuals');

let generateFakeAPIs = () => {
  let decisionsList = [];

  decisionsList.push({
    executionId: executionIds[0],
    executionDate: faker.date.recent(),
    executionType: 'DECISION',
    executedModelName: 'fraud-score',
    executionSucceeded: true,
    executorName: 'Technical User'
  });

  for (let i = 1; i < 10; i++) {
    let executionDate = faker.date.past();

    decisionsList.push({
      executionId: executionIds[i],
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
    saliencies: salienciesData,
    counterfactuals: cfData
  };
};

module.exports = generateFakeAPIs;
