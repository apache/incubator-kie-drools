const fs = require('fs');
const confirmTravelForm = require('./forms/ConfirmTravel');
const applyForVisaForm = require('./forms/ApplyForVisa');

const restData = require('./rest');

module.exports = controller = {
  callCompleteTask: (req, res) => {
    console.log(
      `......ProcessId:${req.params.processId} --piId:${req.params.processId} --taskId:${req.params.taskId}`
    );

    const processId = restData.process.filter(data => {
      return data.processId === req.params.processId;
    });

    const piTasks = processId[0].instances.filter(err => {
      return err.processInstanceId === req.params.processInstanceId;
    });

    const task = piTasks[0].tasks.filter(err => {
      return err.taskId === req.params.taskId;
    });

    const phase = req.query.phase;

    switch (task[0].complete) {
      case 'success':
        let successMessage;

        if (phase) {
          successMessage = `Task '${req.params.taskId}' has successfully finished phase '${phase}'`;
        } else {
          successMessage = `Task '${req.params.taskId}' successfully completed.`;
        }
        res.send(successMessage);
        break;
      case 'failed':
        let failedMessage;
        if (phase) {
          failedMessage = `Task '${req.params.taskId}' couldn't successfully finish phase '${phase}'`;
        } else {
          failedMessage = `Task '${req.params.taskId}' couldn't be completed`;
        }
        res.status(500).send(failedMessage);
        break;
    }
  },

  getTaskForm: (req, res) => {
    console.log(
      `......ProcessId:${req.params.processId} --piId:${req.params.processId} --taskId:${req.params.taskId}`
    );

    const processId = restData.process.filter(data => {
      return data.processId === req.params.processId;
    });

    const piTasks = processId[0].instances.filter(err => {
      return err.processInstanceId === req.params.processInstanceId;
    });

    const task = piTasks[0].tasks.filter(err => {
      return err.taskId === req.params.taskId;
    });

    if (task[0].referenceName === 'ConfirmTravel') {
      res.send(confirmTravelForm);
    } else if (task[0].referenceName === 'VisaApplication') {
      res.send(applyForVisaForm);
    }
  }
};
