// HTTP SERVER
const express = require('express');
var cors = require('cors');
const app = express();
const { ApolloServer, gql } = require('apollo-server-express');
// GraphQL - Apollo
const { GraphQLScalarType } = require('graphql');
const uuidv1 = require('uuid/v1');

// Config
const config = require('./config');

//Mock data
const data = require('./MockData/graphql');
const controller = require('./MockData/controllers');
const typeDefs = require('./MockData/types');

const _ = require('lodash');

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

app.use(
  cors({
    origin: config.corsDomain, // Be sure to switch to your production domain
    optionsSuccessStatus: 200
  })
);

//Rest Api's
// http://localhost:4000/{processId}/{processInstanceId}/{taskName}/{taskId}

app.post(
  '/:processId/:processInstanceId/:taskName/:taskId',
  async (req, res) => {
    await timeout(500);
    controller.callCompleteTask(req, res);
  }
);

app.get(
  '/:processId/:processInstanceId/:taskName/:taskId/schema',
  controller.getTaskForm
);

app.get('/:processId/:taskName/schema', controller.getTaskDefinitionForm);

function timeout(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

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

// Provide resolver functions for your schema fields
const resolvers = {
  Query: {
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

      if (args.pagination) {
        const offset = args.pagination.offset;
        const limit = args.pagination.limit;

        result = result.slice(offset, offset + limit);
      }
      return result;
    },
    ProcessInstances: async (parent, args) => {
      const result = data.ProcessInstances.filter(datum => {
        if (args['where'].id && args['where'].id.equal) {
          return datum.id === args['where'].id.equal;
        } else {
          return false;
        }
      });
      await timeout(2000);
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
