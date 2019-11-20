// HTTP SERVER
const express = require('express');
var cors = require('cors')
const app = express();
const {ApolloServer, gql} = require("apollo-server-express");
// GraphQL - Apollo


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
    console.log(`The server is running and listening at http://localhost:${port}`);
  });
}

app.use(cors({
  origin: config.corsDomain, // Be sure to switch to your production domain
  optionsSuccessStatus: 200
}));

//Rest Api's
// http://localhost:4000/management/process/{processId}/instances/{processInstanceId}/error
app.get('/management/process/:processId/instances/:processInstanceId/error', controller.showError)
app.get('/management/process/:processId/instances/:processInstanceId/skip', controller.callSkip)
app.get('/management/process/:processId/instances/:processInstanceId/retrigger', controller.callRetrigger)



// Provide resolver functions for your schema fields
const resolvers = {
    Query: {
        ProcessInstances: (parent, args, context, info) => {
            const result = data.filter(datum => {
                console.log("args", args["filter"].id);
                console.log("args", args["filter"].parentProcessInstanceId);
                console.log("data", datum.parentProcessInstanceId);
                if (args["filter"].id) {
                    return (datum.id == args["filter"].id);
                } else if (args["filter"].parentProcessInstanceId[0] == null) {
                    return (datum.parentProcessInstanceId == args["filter"].parentProcessInstanceId[0] && args["filter"].state.includes(datum.state))
                } else if (args["filter"].parentProcessInstanceId[0] !== null) {
                    return (datum.parentProcessInstanceId == args["filter"].parentProcessInstanceId[0]);
                }
            });
            return result;
        }
    }
};
  const server = new ApolloServer({typeDefs, resolvers});

  server.applyMiddleware({app});

module.exports =  {
  getApp: () => aasdasdadpp,
  setPort,
  listen
};
