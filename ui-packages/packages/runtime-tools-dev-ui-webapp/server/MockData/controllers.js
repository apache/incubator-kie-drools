const restData = require('./rest');
const graphData = require('./graphql');
const path = require('path');
const _ = require('lodash');
const confirmTravelForm = require('./forms/ConfirmTravel');
const applyForVisaForm = require('./forms/ApplyForVisa');
const emptyForm = require('./forms/EmptyForm');
const formData = require('../MockData/forms/formData');

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


const processSvg = ['8035b580-6ae4-4aa8-9ec0-e18e19809e0b','8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop', '2d962eef-45b8-48a9-ad4e-9cde0ad6af88', 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e']
module.exports = controller = {
  showError: (req, res) => {
    console.log('called', req.params.processId, req.params.processInstanceId);
    const { process } = restData.management;
    const processId = process.filter(data => {
      return data.processId === req.params.processId;
    });
    const error = processId[0].instances.filter(err => {
      return err.processInstanceId === req.params.processInstanceId;
    });
    res.send(error[0].error);
  },
  callRetrigger: (req, res) => {
    const { process } = restData.management;
    const processId = process.filter(data => {
      return data.processId === req.params.processId;
    });
    const error = processId[0].instances.filter(err => {
      return err.processInstanceId === req.params.processInstanceId;
    });
    switch (error[0].retrigger) {
      case 'success':
        res.send(error[0].retrigger);
        break;
      case 'Authentication failed':
        res.status(401).send(error[0].retrigger);
        break;
      case 'Authorization failed':
        res.status(403).send(error[0].retrigger);
        break;
      case 'Internal server error':
        res.status(500).send(error[0].retrigger);
        break;
    }
  },
  callSkip: (req, res) => {
    const { process } = restData.management;
    const processId = process.filter(data => {
      return data.processId === req.params.processId;
    });
    const error = processId[0].instances.filter(err => {
      return err.processInstanceId === req.params.processInstanceId;
    });
    switch (error[0].skip) {
      case 'success':
        res.send(error[0].skip);
        break;
      case 'Authentication failed':
        res.status(401).send(error[0].skip);
        break;
      case 'Authorization failed':
        res.status(403).send(error[0].skip);
        break;
      case 'Internal server error':
        res.status(500).send(error[0].skip);
        break;
    }
  },
  callAbort: (req, res) => {
    const failedAbortInstances = ['8035b580-6ae4-4aa8-9ec0-e18e19809e0b2', '8035b580-6ae4-4aa8-9ec0-e18e19809e0b3']
    const data = graphData.ProcessInstanceData.filter(data => {
      return data.id === req.params.processInstanceId;
    });
    if (failedAbortInstances.includes(data[0].id)) {
      res.status(404).send('process not found');
    } else {
      data[0].state = 'ABORTED';
      res.status(200).send('success');
    }
  },
  callNodeRetrigger: (req, res) => {
    const data = graphData.ProcessInstanceData.filter(data => {
      return data.id === req.params.processInstanceId;
    });
    const nodeObject = data[0].nodes.filter(node => node.id === req.params.nodeInstanceId);
    if (nodeObject[0].name.includes('not found')) {
      res.status(404).send('node not found')
    }
    else {
      nodeObject[0].enter = new Date().toISOString();
      res.status(200).send(data[0]);
    }
  },
  callNodeCancel: (req, res) => {
    const data = graphData.ProcessInstanceData.filter(data => {
      return data.id === req.params.processInstanceId;
    });
    const nodeObject = data[0].nodes.filter(node => node.id === req.params.nodeInstanceId);
    if (nodeObject[0].name.includes('not found')) {
      res.status(404).send('node not found')
    }
    else {
      nodeObject[0].exit = new Date().toISOString();
      res.status(200).send(data[0]);
    }
  },
  handleJobReschedule: (req, res) => {
    const data = graphData.JobsData.find(data => {
      return data.id === req.params.id;
    });
    if (req.params.id !== "eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0" && req.body.repeatInterval && req.body.repeatLimit) {
      data.expirationTime = req.body.expirationTime;
      data.repeatInterval = req.body.repeatInterval;
      data.repeatLimit = req.body.repeatLimit;
    } else {
      if (req.params.id !== "eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0") {
        data.expirationTime = req.body.expirationTime;
      }
    }
    if (req.params.id !== "eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0") {
      res.status(200).send(data);
    } else {
      res.status(400).send('job not rescheduled')
    }
  },

  callNodeTrigger: (req, res) => {
    const graphData = require('./graphql');
    const processInstance = graphData.ProcessInstanceData.filter((process) => {
      return process.id === req.params.processInstanceId
    });
    const nodeObject = processInstance[0].nodes.filter((node, index) => {
      if (index !== processInstance[0].nodes.length - 1) {
        return node.definitionId === req.params.nodeId
      }
    });

    if (nodeObject.length === 0) {
      res.status(404).send('node not found');
    } else {
      const node = { ...nodeObject[0] };
      node.enter = new Date().toISOString();
      node.exit = null;
      processInstance[0].nodes.unshift(node);
      res.status(200).send({});
    }
  },

  getTriggerableNodes: (req, res) => {
    if (req.params.processId !== null || req.params.processId !== undefined) {
      res.send([
        {
          nodeDefinitionId: "_BDA56801-1155-4AF2-94D4-7DAADED2E3C0",
          name: "Send visa application",
          id: 1,
          type: "ActionNode",
          uniqueId: "1"
        },
        {
          nodeDefinitionId: "_175DC79D-C2F1-4B28-BE2D-B583DFABF70D",
          name: "Book",
          id: 2,
          type: "Split",
          uniqueId: "2"
        },
        {
          nodeDefinitionId: "_E611283E-30B0-46B9-8305-768A002C7518",
          name: "visasrejected",
          id: 3,
          type: "EventNode",
          uniqueId: "3"
        }
      ])
    } else {
      res.send([])
    }
  },

  callJobCancel: (req, res) => {
    const mockFailedJobs = ['dad3aa88-5c1e-4858-a919-6123c675a0fa_0']
    const graphData = require('./graphql');
    const jobData = graphData.JobsData.filter(job => job.id === req.params.jobId);
    if (mockFailedJobs.includes(jobData[0].id) || jobData.length === 0) {
      res.status(404).send('job not found')
    } else {
      jobData[0].status = 'CANCELED';
      jobData[0].lastUpdate = new Date().toISOString();
      res.status(200).send(jobData[0]);
    }
  },
  dispatchSVG: (req, res) => {
    try {
      if(processSvg.includes(req.params.id)){
        if(req.params.processId === 'travels') {
          res.sendFile(path.resolve(__dirname+'/../static/travels.svg'))
        } else if (req.params.processId === 'flightBooking') {
          res.sendFile(path.resolve(__dirname+'/../static/flightBooking.svg'))
        } else if (req.params.processId === 'hotelBooking') {
          res.sendFile(path.resolve(__dirname+'/../static/hotelBooking.svg'))
        }
      } else {
        res.send(null);
      }
    } catch(error){
      res.status(404).send(error)
    }
  },
  callCompleteTask: (req, res) => {
    console.log(
      `......ProcessId:${req.params.processId} --piId:${req.params.processId} --taskId:${req.params.taskId}`
    );

    const processId = restData.process.filter(data => {
      return data.processId === req.params.processId;
    });

    const task = graphData.UserTaskInstances.find(userTask => {
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

    const task = graphData.UserTaskInstances.find(userTask => {
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
      res.status(500).send('Error: cannot load form');
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