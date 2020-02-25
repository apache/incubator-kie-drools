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
app.post(
  '/management/processes/:processId/instances/:processInstanceId',
  controller.callAbort
);

function timeout(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

// Provide resolver functions for your schema fields
const resolvers = {
  Query: {
    ProcessInstances: async (parent, args) => {
      const result = data.filter(datum => {
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
          return (
            datum.parentProcessInstanceId == null &&
            args['where'].state.in.includes(datum.state)
          );
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
