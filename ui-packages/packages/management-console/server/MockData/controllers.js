const restData = require('./rest');
const graphData = require('./graphql');

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
      if(req.params.id !== "eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0") {
        data.expirationTime = req.body.expirationTime;
      }
    }
    if(req.params.id !== "eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0") {
      res.status(200).send(data);
    } else {
      res.status(400).send('job not rescheduled')
    }
  }
};
