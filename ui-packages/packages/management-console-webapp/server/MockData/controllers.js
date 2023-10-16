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
const restData = require('./rest');
const graphData = require('./graphql');
const path = require('path');

const processSvg = [
  '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
  'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
  '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
  'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
];
module.exports = controller = {
  showError: (req, res) => {
    console.log('called', req.params.processId, req.params.processInstanceId);
    const { process } = restData.management;
    const processId = process.filter((data) => {
      return data.processId === req.params.processId;
    });
    const error = processId[0].instances.filter((err) => {
      return err.processInstanceId === req.params.processInstanceId;
    });
    res.send(error[0].error);
  },
  callRetrigger: (req, res) => {
    const { process } = restData.management;
    const processId = process.filter((data) => {
      return data.processId === req.params.processId;
    });
    const error = processId[0].instances.filter((err) => {
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
    const processId = process.filter((data) => {
      return data.processId === req.params.processId;
    });
    const error = processId[0].instances.filter((err) => {
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
    const failedAbortInstances = [
      '8035b580-6ae4-4aa8-9ec0-e18e19809e0b2',
      '8035b580-6ae4-4aa8-9ec0-e18e19809e0b3'
    ];
    const data = graphData.ProcessInstanceData.filter((data) => {
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
    const data = graphData.ProcessInstanceData.filter((data) => {
      return data.id === req.params.processInstanceId;
    });
    const nodeObject = data[0].nodes.filter(
      (node) => node.id === req.params.nodeInstanceId
    );
    if (nodeObject[0].name.includes('not found')) {
      res.status(404).send('node not found');
    } else {
      nodeObject[0].enter = new Date().toISOString();
      res.status(200).send(data[0]);
    }
  },
  callNodeCancel: (req, res) => {
    const data = graphData.ProcessInstanceData.filter((data) => {
      return data.id === req.params.processInstanceId;
    });
    const nodeObject = data[0].nodes.filter(
      (node) => node.id === req.params.nodeInstanceId
    );
    if (nodeObject[0].name.includes('not found')) {
      res.status(404).send('node not found');
    } else {
      nodeObject[0].exit = new Date().toISOString();
      res.status(200).send(data[0]);
    }
  },
  handleJobReschedule: (req, res) => {
    const data = graphData.JobsData.find((data) => {
      return data.id === req.params.id;
    });
    if (
      req.params.id !== 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0' &&
      req.body.repeatInterval &&
      req.body.repeatLimit
    ) {
      data.expirationTime = req.body.expirationTime;
      data.repeatInterval = req.body.repeatInterval;
      data.repeatLimit = req.body.repeatLimit;
    } else {
      if (req.params.id !== 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0') {
        data.expirationTime = req.body.expirationTime;
      }
    }
    if (req.params.id !== 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0') {
      res.status(200).send(data);
    } else {
      res.status(400).send('job not rescheduled');
    }
  },

  callNodeTrigger: (req, res) => {
    const processInstance = graphData.ProcessInstanceData.filter((process) => {
      return process.id === req.params.processInstanceId;
    });
    const nodeObject = processInstance[0].nodes.filter((node, index) => {
      if (index !== processInstance[0].nodes.length - 1) {
        return node.id === req.params.nodeId;
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
          nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
          name: 'Send visa application',
          id: 1,
          type: 'ActionNode',
          uniqueId: '1'
        },
        {
          nodeDefinitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D',
          name: 'Book',
          id: 2,
          type: 'Split',
          uniqueId: '2'
        },
        {
          nodeDefinitionId: '_E611283E-30B0-46B9-8305-768A002C7518',
          name: 'visasrejected',
          id: 3,
          type: 'EventNode',
          uniqueId: '3'
        }
      ]);
    } else {
      res.send([]);
    }
  },

  callJobCancel: (req, res) => {
    const mockFailedJobs = ['dad3aa88-5c1e-4858-a919-6123c675a0fa_0'];
    const graphData = require('./graphql');
    const jobData = graphData.JobsData.filter(
      (job) => job.id === req.params.jobId
    );
    if (mockFailedJobs.includes(jobData[0].id) || jobData.length === 0) {
      res.status(404).send('job not found');
    } else {
      jobData[0].status = 'CANCELED';
      jobData[0].lastUpdate = new Date().toISOString();
      res.status(200).send(jobData[0]);
    }
  },
  dispatchSVG: (req, res) => {
    try {
      if (processSvg.includes(req.params.id)) {
        if (req.params.processId === 'travels') {
          console.log('travels');
          res.sendFile(path.resolve(__dirname + '/../static/travels.svg'));
        } else if (req.params.processId === 'flightBooking') {
          res.sendFile(
            path.resolve(__dirname + '/../static/flightBooking.svg')
          );
        } else if (req.params.processId === 'hotelBooking') {
          res.sendFile(path.resolve(__dirname + '/../static/hotelBooking.svg'));
        }
      } else {
        res.send(null);
      }
    } catch (error) {
      res.status(404).send(error);
    }
  }
};
