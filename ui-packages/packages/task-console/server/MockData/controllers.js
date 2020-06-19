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

    switch (task[0].complete) {
      case 'success':
        res.send(task[0].message);
        break;
      case 'failed':
        res.status(500).send(task[0].message);
        break;
    }
  }
};
