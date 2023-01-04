/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

app.get('/:processId/:taskName/schema', controller.getTaskDefinitionForm);

const taskDetailsError = ['5cead49f-7649-410a-89ff-840cc52adf52'];

function timeout(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
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

// Provide resolver functions for your schema fields
const resolvers = {
  Query: {
    UserTaskInstances: async (parent, args) => {
      let result = data.UserTaskInstances.filter((datum) => {
        console.log('args', args);

        if (args['where'].and) {
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
          return true;
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
    },
    ProcessInstances: async (parent, args) => {
      const result = data.ProcessInstances.filter((datum) => {
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
