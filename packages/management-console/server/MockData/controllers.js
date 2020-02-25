const restData = require('./rest');

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
    // res.send(error[0].retrigger)
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
    const graphData = require('./graphql');
    const data = graphData.filter(data => {
      return data.id === req.params.processInstanceId;
    });
    if (data.length === 0) {
      res.status(500).send('Internal server error');
    } else {
      data[0].state = 'ABORTED';
      res.status(200).send('success');
    }
  }
};
