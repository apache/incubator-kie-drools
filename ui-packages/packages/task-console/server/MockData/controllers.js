const fs = require('fs');
const _ = require('lodash');
const graphQL = require('./graphql');
const confirmTravelForm = require('./forms/ConfirmTravel');
const applyForVisaForm = require('./forms/ApplyForVisa');
const emptyForm = require('./forms/EmptyForm');

const restData = require('./rest');

const tasksUnableToTransition = [
  '047ec38d-5d57-4330-8c8d-9bd67b53a529',
  '841b9dba-3d91-4725-9de3-f9f4853b417e'
];

const taskWithoutForm = [
  '475e3eb3-1de4-4f68-a146-79c236353a03',
  '615b9143-1468-4028-b454-6122e2139f5c'
];

const taskWithEmptyForm = [
  '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  '809aae9e-f0bf-4892-b0c9-4be80664d2aa'
];

module.exports = controller = {
  callCompleteTask: (req, res) => {
    console.log(
      `......ProcessId:${req.params.processId} --piId:${req.params.processId} --taskId:${req.params.taskId}`
    );

    const processId = restData.process.filter(data => {
      return data.processId === req.params.processId;
    });

    const task = graphQL.UserTaskInstances.find(userTask => {
      return userTask.id === req.params.taskId;
    });

    if (tasksUnableToTransition.includes(task.id)) {
      res.status(500).send('Unexpected failure when doing a transition!');
    } else {
      const phase = req.query.phase;

      if (phase === 'complete') {
        task.state = 'Completed';
        task.completed = new Date().toISOString();
      }

      res.send(task.inputs);
    }
  },

  getTaskForm: (req, res) => {
    console.log(
      `......ProcessId:${req.params.processId} --piId:${req.params.processInstanceId} --taskId:${req.params.taskId}`
    );

    const task = graphQL.UserTaskInstances.find(userTask => {
      return userTask.id === req.params.taskId;
    });

    const clearPhases = task.completed || task.state === 'Aborted';

    if (taskWithEmptyForm.includes(task.id)) {
      const form = _.cloneDeep(emptyForm);
      if (clearPhases) {
        delete form.phases;
      }
      res.send(JSON.stringify(form));
      return;
    }

    if (taskWithoutForm.includes(task.id)) {
      res.status(500).send('');
      return;
    }

    res.send(JSON.stringify(getTaskSchema(task.name, clearPhases)));
  },

  getTaskDefinitionForm: (req, res) => {
    console.log(
      `......ProcessId:${req.params.processId} --TaskName:${req.params.taskName}`
    );

    res.send(JSON.stringify(getTaskSchema(req.params.taskName, true)));
  }
};

function getTaskSchema(taskName, clearPhases) {
  let schema;

  console.log(
    `Getting Schema for task: ${taskName} --clearPhases: ${clearPhases}`
  );

  switch (taskName) {
    case 'ConfirmTravel': {
      schema = _.cloneDeep(confirmTravelForm);
      break;
    }
    case 'VisaApplication': {
      schema = _.cloneDeep(applyForVisaForm);
      break;
    }
  }

  if (clearPhases) {
    delete schema.phases;
  }

  return schema;
}
