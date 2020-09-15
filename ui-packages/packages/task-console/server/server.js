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
  controller.callCompleteTask
);

app.get(
  '/:processId/:processInstanceId/:taskName/:taskId/schema',
  controller.getTaskForm
);

app.get(
  '/:processId/:taskName/schema',
  controller.getTaskDefinitionForm
);

function timeout(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

// Provide resolver functions for your schema fields
const resolvers = {
  Query: {
    UserTaskInstances: async (parent, args) => {
      let result = data.UserTaskInstances.filter(datum => {
        console.log('args', args)

        if (args['where'].state && args['where'].state.in) {
          return args['where'].state.in.includes(datum.state);
        } else {
          // searching for tasks assigned to current user
          return true;
        }

        return false;
      });
      if (args['orderBy']) {
        console.log('sort by:', args['orderBy']);
        result = _.orderBy(
          result,
          _.keys(args['orderBy']).map(key => key.toLowerCase()),
          _.values(args['orderBy']).map(value => value.toLowerCase())
        );
      }
      await timeout(2000);

      if (args.pagination) {
        const offset = args.pagination.offset;
        const limit = args.pagination.limit;

        result = result.slice(offset, offset + limit);
      }

      console.log('result length: ' + result.length);
      return result;
    },
    ProcessInstances: async (parent, args) => {
      const result = data.ProcessInstances.filter(datum => {
        console.log('args', args['where']);
        if (args['where'].id && args['where'].id.equal) {
          return datum.id == args['where'].id.equal;
        } else {
          return false;
        }
      });
      await timeout(2000);
      console.log('result length: ' + result.length);
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
