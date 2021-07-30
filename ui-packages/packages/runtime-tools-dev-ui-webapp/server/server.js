// HTTP SERVER
const express = require('express');
var cors = require('cors');
const app = express();
const { ApolloServer, gql } = require('apollo-server-express');
var bodyParser = require('body-parser')
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
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())
app.use(
  cors({
    origin: config.corsDomain, // Be sure to switch to your production domain
    optionsSuccessStatus: 200
  })
);

//Rest Api's
// http://localhost:4000/management/processes/{processId}/instances/{processInstanceId}/error
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
app.post('/management/processes/:processId/instances/:processInstanceId/nodeInstances/:nodeInstanceId',
  controller.callNodeRetrigger
);
app.delete('/management/processes/:processId/instances/:processInstanceId/nodeInstances/:nodeInstanceId',
  controller.callNodeCancel
);
app.patch('/jobs/:id', controller.handleJobReschedule);
app.post('/management/processes/:processId/instances/:processInstanceId/nodes/:nodeId',
  controller.callNodeTrigger
);
app.get('/management/processes/:processId/nodes', controller.getTriggerableNodes)
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
      potentialGroupsClause.potentialGroups.containsAny.some(clauseGroup =>
        userTaskInstance.potentialGroups.includes(clauseGroup)
      )
    ) {
      return true;
    }
  }
};

function timeout(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
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
// Provide resolver functions for your schema fields
const resolvers = {
  Query: {
    ProcessInstances: async (parent, args) => {
      let result = data.ProcessInstanceData.filter(datum => {
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
        } else if (args['where'].parentProcessInstanceId.equal) {
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
        console.log('orderBy args: ', args['orderBy'])
        result = _.orderBy(
          result,
          _.keys(args['orderBy']).map(key => key),
          _.values(args['orderBy']).map(value => value.toLowerCase())
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
        const result = data.JobsData.filter(jobData => {
          console.log('Job data args->', args['where'].processInstanceId)
          if (args['where'].processInstanceId && args['where'].processInstanceId.equal) {
            return jobData.processInstanceId == args['where'].processInstanceId.equal;
          } else if (args['where'].status && args['where'].status.in) {
            return args['where'].status.in.includes(jobData.status)
          }
        });
        console.log('orderby', args['orderBy'])
        await timeout(2000);
        if (args['orderBy'] && Object.values(args['orderBy'])[0] === 'ASC') {
          const orderArg = Object.keys(args['orderBy'])[0]
          result.sort((a, b) => {
            if (orderArg === 'lastUpdate' || orderArg === 'expirationTime') {
              return new Date(a[orderArg]) - new Date(b[orderArg])
            } else if (orderArg === 'status') {
              return a[orderArg].localeCompare(b[orderArg])
            } else {
              return a[orderArg] - b[orderArg];
            }
          })
        } else if (args['orderBy'] && Object.values(args['orderBy'])[0] === 'DESC') {
          const orderArg = Object.keys(args['orderBy'])[0]
          result.sort((a, b) => {
            if (orderArg === 'lastUpdate' || orderArg === 'expirationTime') {
              return new Date(b[orderArg]) - new Date(a[orderArg])
            } else if (orderArg === 'status') {
              return b[orderArg].localeCompare(a[orderArg])
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
      let result = data.UserTaskInstances.filter(datum => {
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
          _.keys(args['orderBy']).map(key => key),
          _.values(args['orderBy']).map(value => value.toLowerCase())
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
