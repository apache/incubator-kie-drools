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
// HTTP SERVER
const express = require('express');
const swaggerUi = require('swagger-ui-express');
const swaggerApiDoc = require('./MockData/openAPI/openapi.json');
var cors = require('cors');
const app = express();
const { ApolloServer, gql } = require('apollo-server-express');
var bodyParser = require('body-parser');
// GraphQL - Apollo
const { GraphQLScalarType } = require('graphql');
const uuidv1 = require('uuid/v1');
const _ = require('lodash');
// Config
const config = require('./config');

//Mock data
const data = require('./MockData/graphql');
const controller = require('./MockData/controllers');
const typeDefs = require('./MockData/types');
const mutationRestData = require('./MockData/mutationRest');

const swaggerOptions = {
  swaggerOptions: {
    url: '/q/openapi.json'
  }
};

function setPort(port = 4000) {
  app.set('port', parseInt(port, 10));
}

function listen() {
  const port = app.get('port') || config.port;
  app.listen(port, () => {
    console.log(
      `The server is running and listening at http://localhost:${port}`
    );
  });
}
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }));

// parse application/json
app.use(bodyParser.json());
app.use(
  cors({
    origin: config.corsDomain, // Be sure to switch to your production domain
    optionsSuccessStatus: 200
  })
);
app.get('/q/openapi.json', (req, res) => res.json(swaggerApiDoc));
app.use(
  '/docs',
  swaggerUi.serveFiles(null, swaggerOptions),
  swaggerUi.setup(null, swaggerOptions)
);

//Rest Api's
// http://localhost:4000/management/processes/{processId}/instances/{processInstanceId}/error

app.post('/', controller.triggerCloudEvent);
app.put('/', controller.triggerCloudEvent);

app.post(
  '/management/processes/:processId/instances/:processInstanceId/error',
  controller.showError
);
app.post(
  '/management/processes/:processId/instances/:processInstanceId/skip',
  controller.callSkip
);
app.post(
  '/management/processes/:processId/instances/:processInstanceId/retrigger',
  controller.callRetrigger
);
app.delete(
  '/management/processes/:processId/instances/:processInstanceId',
  controller.callAbort
);
app.post(
  '/management/processes/:processId/instances/:processInstanceId/nodeInstances/:nodeInstanceId',
  controller.callNodeRetrigger
);
app.delete(
  '/management/processes/:processId/instances/:processInstanceId/nodeInstances/:nodeInstanceId',
  controller.callNodeCancel
);
app.patch('/jobs/:id', controller.handleJobReschedule);
app.post(
  '/management/processes/:processId/instances/:processInstanceId/nodes/:nodeId',
  controller.callNodeTrigger
);
app.get(
  '/management/processes/:processId/nodes',
  controller.getTriggerableNodes
);
app.delete('/jobs/:jobId', controller.callJobCancel);
app.get('/svg/processes/:processId/instances/:id', controller.dispatchSVG);

app.post(
  '/:processId/:processInstanceId/:taskName/:taskId',
  controller.callCompleteTask
);

app.get(
  '/:processId/:processInstanceId/:taskName/:taskId/schema',
  controller.getTaskForm
);

app.get('/:processId/:taskName/schema', controller.getTaskDefinitionForm);

app.get('/forms/list', controller.getForms);
app.get('/customDashboard/list', controller.getCustomDashboards);
app.get('/customDashboard/:name', controller.getCustomDashboardContent);
app.get('/forms/:formName', controller.getFormContent);
app.post('/forms/:formName', controller.saveFormContent);

app.post('/hiring', controller.startProcessInstance);
app.get('/:processName/schema', controller.getProcessFormSchema);

const taskDetailsError = ['5cead49f-7649-410a-89ff-840cc52adf52'];

const checkStatesFilters = (userTaskInstance, states) => {
  return states.includes(userTaskInstance.state);
};

const checkTaskNameFilters = (userTaskInstance, names) => {
  for (let i = 0; i < names.length; i++) {
    let name = names[i].referenceName.like.toLowerCase();
    name = name.substring(1, name.length - 1);

    if (
      userTaskInstance.referenceName &&
      userTaskInstance.referenceName.toLowerCase().includes(name)
    ) {
      return true;
    }
  }

  return false;
};

const checkTaskAssignment = (userTaskInstance, assignments) => {
  const actualOwnerClause = assignments.or[0];
  if (actualOwnerClause.actualOwner.equal === userTaskInstance.actualOwner) {
    return true;
  }
  if (userTaskInstance.actualOwner === null) {
    const excludedUsersClause = assignments.or[1].and[1].not;

    if (
      userTaskInstance.excludedUsers &&
      userTaskInstance.excludedUsers.includes(
        excludedUsersClause.excludedUsers.contains
      )
    ) {
      return false;
    }

    const potentialUsersClause = assignments.or[1].and[2].or[0];
    if (
      userTaskInstance.potentialUsers.includes(
        potentialUsersClause.potentialUsers.contains
      )
    ) {
      return true;
    }
    const potentialGroupsClause = assignments.or[1].and[2].or[1];
    if (
      potentialGroupsClause.potentialGroups.containsAny.some((clauseGroup) =>
        userTaskInstance.potentialGroups.includes(clauseGroup)
      )
    ) {
      return true;
    }
  }
};

function timeout(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function paginatedResult(arr, offset, limit) {
  let paginatedArray = arr.slice(offset, offset + limit);
  console.log('offset : ', offset);
  console.log('limit : ', limit);
  if (offset > arr.length && paginatedArray.length === 0) {
    let prevData = arr.slice(offset - limit, limit);
    return prevData;
  }
  return paginatedArray;
}

function setProcessInstanceState(processInstanceId, state) {
  const processInstance = data.ProcessInstanceData.filter(
    (data) => data.id === processInstanceId
  );
  processInstance[0].state = state;
}

const processSvg = [
  '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
  'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
  '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
  'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
];
const fs = require('fs');

//init svg
data.ProcessInstanceData.forEach((datum) => {
  if (processSvg.includes(datum.id)) {
    if (datum.processId === 'travels') {
      console.log('travels');
      datum.diagram = fs.readFileSync(
        __dirname + '/static/travels.svg',
        'utf8'
      );
    } else if (datum.processId === 'flightBooking') {
      datum.diagram = fs.readFileSync(__dirname + '/static/flightBooking.svg');
    } else if (datum.processId === 'hotelBooking') {
      datum.diagram = fs.readFileSync(__dirname + '/static/hotelBooking.svg');
    }
  } else {
    datum.diagram = null;
  }
  if (datum.processId !== null || datum.processId !== undefined) {
    datum.nodeDefinitions = [
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
    ];
  } else {
    datum.nodeDefinition = null;
  }
});

// Provide resolver functions for your schema fields
const resolvers = {
  Mutation: {
    ProcessInstanceRetry: async (parent, args) => {
      const successRetryInstances = [
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b2',
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b3'
      ];
      const { process } = mutationRestData.management;
      const processInstance = process.filter((data) => {
        return data.processInstanceId === args['id'];
      });
      if (successRetryInstances.includes(processInstance[0].id)) {
        setProcessInstanceState(processInstance[0].processInstanceId, 'ACTIVE');
        processInstance[0].state = 'ACTIVE';
      }
      return processInstance[0].retrigger;
    },
    ProcessInstanceSkip: async (parent, args) => {
      const { process } = mutationRestData.management;
      const processInstance = process.filter((data) => {
        return data.processInstanceId === args['id'];
      });

      return processInstance[0].skip;
    },

    ProcessInstanceAbort: async (parent, args) => {
      const failedAbortInstances = [
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b2',
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b3'
      ];
      const { process } = mutationRestData.management;
      const processInstance = process.filter((data) => {
        return data.processInstanceId === args['id'];
      });
      if (failedAbortInstances.includes(processInstance[0].id)) {
        return 'process not found';
      } else {
        setProcessInstanceState(
          processInstance[0].processInstanceId,
          'ABORTED'
        );
        processInstance[0].state = 'ABORTED';
        return processInstance[0].abort;
      }
    },

    ProcessInstanceUpdateVariables: async (parent, args) => {
      const processInstance = data.ProcessInstanceData.filter((datum) => {
        return datum.id === args['id'];
      });
      processInstance[0].variables = args['variables'];
      return processInstance[0].variables;
    },

    NodeInstanceTrigger: async (parent, args) => {
      const nodeData = data.ProcessInstanceData.filter((data) => {
        return data.id === args['id'];
      });
      const nodeObject = nodeData[0].nodes.filter((node, index) => {
        if (index !== nodeData[0].nodes.length - 1) {
          return node.id === args['nodeId'];
        }
      });
      if (nodeObject.length === 0) {
        throw new Error('node not found');
      } else {
        const node = { ...nodeObject[0] };
        node.enter = new Date().toISOString();
        node.exit = null;
        nodeData[0].nodes.unshift(node);
        return nodeData[0];
      }
    },

    NodeInstanceCancel: async (parent, args) => {
      const nodeData = data.ProcessInstanceData.filter((data) => {
        return data.id === args['id'];
      });
      const nodeObject = nodeData[0].nodes.filter(
        (node) => node.id === args['nodeInstanceId']
      );
      if (nodeObject[0].name.includes('not found')) {
        throw new Error('node not found');
      } else {
        nodeObject[0].exit = new Date().toISOString();
        return nodeObject[0];
      }
    },

    NodeInstanceRetrigger: async (parent, args) => {
      const nodeData = data.ProcessInstanceData.filter((data) => {
        return data.id === args['id'];
      });
      const nodeObject = nodeData[0].nodes.filter(
        (node) => node.id === args['nodeInstanceId']
      );
      if (nodeObject[0].name.includes('not found')) {
        throw new Error('node not found');
      } else {
        nodeObject[0].exit = new Date().toISOString();
        return nodeObject[0];
      }
    },

    JobCancel: async (parent, args) => {
      const mockFailedJobs = ['dad3aa88-5c1e-4858-a919-6123c675a0fa_0'];
      const jobData = data.JobsData.filter((job) => job.id === args['id']);
      if (mockFailedJobs.includes(jobData[0].id) || jobData.length === 0) {
        return 'job not found';
      } else {
        jobData[0].status = 'CANCELED';
        jobData[0].lastUpdate = new Date().toISOString();
        return jobData[0];
      }
    },

    JobReschedule: async (parent, args) => {
      const data = data.JobsData.find((data) => {
        return data.id === args['id'];
      });
      if (
        args['id'] !== 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0' &&
        args['data'].repeatInterval &&
        args['data'].repeatLimit
      ) {
        data.expirationTime = args['data'].expirationTime;
        data.repeatInterval = args['data'].repeatInterval;
        data.repeatLimit = args['data'].repeatLimit;
      } else {
        if (args['id'] !== 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0') {
          data.expirationTime = args['data'].expirationTime;
        }
      }
      if (args['id'] !== 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0') {
        return data;
      } else {
        return 'job not rescheduled';
      }
    }
  },

  Query: {
    ProcessInstances: async (parent, args) => {
      let result = data.ProcessInstanceData.filter((datum) => {
        console.log('args', args['where']);
        if (args['where'].id && args['where'].id.equal) {
          return datum.id == args['where'].id.equal;
        } else if (
          args['where'].rootProcessInstanceId &&
          args['where'].rootProcessInstanceId.equal
        ) {
          return (
            datum.rootProcessInstanceId ==
            args['where'].rootProcessInstanceId.equal
          );
        } else if (
          args['where'].parentProcessInstanceId &&
          args['where'].parentProcessInstanceId.equal
        ) {
          return (
            datum.parentProcessInstanceId ==
            args['where'].parentProcessInstanceId.equal
          );
        } else if (args['where'].parentProcessInstanceId.isNull) {
          if (
            args['where'].or === undefined ||
            (args['where'].or && args['where'].or.length === 0)
          ) {
            return (
              datum.parentProcessInstanceId == null &&
              args['where'].state.in.includes(datum.state)
            );
          } else {
            if (
              datum.parentProcessInstanceId === null &&
              args['where'].state.in.includes(datum.state) &&
              datum.businessKey !== null
            ) {
              for (let i = 0; i < args['where'].or.length; i++) {
                if (
                  datum.businessKey &&
                  datum.businessKey
                    .toLowerCase()
                    .indexOf(
                      args['where'].or[i].businessKey.like.toLowerCase()
                    ) > -1
                ) {
                  return true;
                }
              }
              return false;
            }
          }
        } else {
          return false;
        }
      });
      if (args['orderBy']) {
        console.log('orderBy args: ', args['orderBy']);
        result = _.orderBy(
          result,
          _.keys(args['orderBy']).map((key) => key),
          _.values(args['orderBy']).map((value) => value.toLowerCase())
        );
      }
      await timeout(2000);
      if (args['pagination']) {
        result = paginatedResult(
          result,
          args['pagination'].offset,
          args['pagination'].limit
        );
      }
      console.log('result length: ' + result.length);
      return result;
    },
    Jobs: async (parent, args) => {
      if (Object.keys(args).length > 0) {
        const result = data.JobsData.filter((jobData) => {
          console.log('Job data args->', args['where'].processInstanceId);
          if (
            args['where'].processInstanceId &&
            args['where'].processInstanceId.equal
          ) {
            return (
              jobData.processInstanceId == args['where'].processInstanceId.equal
            );
          } else if (args['where'].status && args['where'].status.in) {
            return args['where'].status.in.includes(jobData.status);
          }
        });
        console.log('orderby', args['orderBy']);
        await timeout(2000);
        if (args['orderBy'] && Object.values(args['orderBy'])[0] === 'ASC') {
          const orderArg = Object.keys(args['orderBy'])[0];
          result.sort((a, b) => {
            if (orderArg === 'lastUpdate' || orderArg === 'expirationTime') {
              return new Date(a[orderArg]) - new Date(b[orderArg]);
            } else if (orderArg === 'status') {
              return a[orderArg].localeCompare(b[orderArg]);
            } else {
              return a[orderArg] - b[orderArg];
            }
          });
        } else if (
          args['orderBy'] &&
          Object.values(args['orderBy'])[0] === 'DESC'
        ) {
          const orderArg = Object.keys(args['orderBy'])[0];
          result.sort((a, b) => {
            if (orderArg === 'lastUpdate' || orderArg === 'expirationTime') {
              return new Date(b[orderArg]) - new Date(a[orderArg]);
            } else if (orderArg === 'status') {
              return b[orderArg].localeCompare(a[orderArg]);
            } else {
              return b[orderArg] - a[orderArg];
            }
          });
        }
        if (args['pagination']) {
          return paginatedResult(
            result,
            args['pagination'].offset,
            args['pagination'].limit
          );
        }

        return result;
      }
    },
    UserTaskInstances: async (parent, args) => {
      let result = data.UserTaskInstances.filter((datum) => {
        console.log('args', args);

        if (args['where'].and) {
          // if filter available
          if (!checkTaskAssignment(datum, args['where'].and[0])) {
            return false;
          }
          if (args['where'].and[1].and.length === 2) {
            // if filter by state and taskNames
            return (
              checkTaskNameFilters(datum, args['where'].and[1].and[1].or) &&
              checkStatesFilters(datum, args['where'].and[1].and[0].state.in)
            );
          } else if (args['where'].and[1].and.length === 1) {
            if (args['where'].and[1].and[0].state) {
              // if filter by states only
              return checkStatesFilters(
                datum,
                args['where'].and[1].and[0].state.in
              );
            } else if (args['where'].and[1].and[0].or) {
              // if filter by taskNames only
              return checkTaskNameFilters(
                datum,
                args['where'].and[1].and[0].or
              );
            }
          } else if (args['where'].and[1].and.length === 0) {
            return false;
          }
        } else if (args['where'].or) {
          // if no filters
          return checkTaskAssignment(datum, args['where']);
        } else if (args['where'].id && args['where'].id.equal) {
          // mock to return single id
          return datum.id === args['where'].id.equal;
        }
        return false;
      });

      if (args['orderBy']) {
        result = _.orderBy(
          result,
          _.keys(args['orderBy']).map((key) => key),
          _.values(args['orderBy']).map((value) => value.toLowerCase())
        );
      }
      await timeout(2000);

      if (args.where.id && taskDetailsError.includes(args.where.id.equal)) {
        throw new Error(`Cannot find task ${args.where.id.equal}`);
      }
      if (args.pagination) {
        const offset = args.pagination.offset;
        const limit = args.pagination.limit;

        result = result.slice(offset, offset + limit);
      }
      return result;
    }
  },

  DateTime: new GraphQLScalarType({
    name: 'DateTime',
    description: 'DateTime custom scalar type',
    parseValue(value) {
      return value;
    },
    serialize(value) {
      return value;
    },
    parseLiteral(ast) {
      return null;
    }
  })
};

const mocks = {
  DateTime: () => new Date().toUTCString(),
  Travels: () => ({
    id: () => uuidv1()
  }),
  VisaApplications: () => ({
    id: () => uuidv1()
  })
};

const server = new ApolloServer({
  typeDefs,
  resolvers,
  mocks,
  mockEntireSchema: false,
  introspection: true,
  playground: true
});

server.applyMiddleware({ app });

module.exports = {
  getApp: () => app,
  setPort,
  listen
};
