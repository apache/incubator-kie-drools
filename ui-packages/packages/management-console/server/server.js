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

      await timeout(2000);
      if (args['pagination']) {
        return paginatedResult(
          result,
          args['pagination'].offset,
          args['pagination'].limit
        );
      }
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
