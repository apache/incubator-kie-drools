module.exports = data = [
  {
    id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
    processId: 'hotelBooking',
    businessKey: 'T1234HotelBooking01',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      businessKey: 'T1234'
    },
    processName: 'HotelBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: 'COMPLETED',
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
      message: 'some thing went wrong'
    },
    addons: [],
    variables:
      '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
        enter: '2019-10-22T03:37:30.798Z',
        exit: '2019-10-22T03:37:30.798Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book hotel',
        definitionId: 'ServiceTask_1',
        id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
        enter: '2019-10-22T03:37:30.795Z',
        exit: '2019-10-22T03:37:30.798Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '2',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
        enter: '2019-10-22T03:37:30.793Z',
        exit: '2019-10-22T03:37:30.795Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863',
    processId: 'flightBooking',
    businessKey: 'T1234FlightBooking01',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      businessKey: 'T1234'
    },
    processName: 'FlightBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: 'ERROR',
    addons: ['jobs-management', 'prometheus-monitoring'],
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: '_a23e6c20-02c2-4c2b-8c5c-e988a0adf87c',
      message:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim idest laborum.'
    },
    start: '2019-10-22T03:40:44.089Z',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'e6c20-02c2-4c2b-8c5c-e988a0adf87c',
        id: '8ac1fc9d-6de2-4b23-864e-ba79315db317',
        enter: '2019-10-22T03:37:30.804Z',
        exit: '2019-10-22T03:37:30.804Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book flight',
        definitionId: '_a23e6c20-02c2-4c2b-8c5c-e988a0adf87c',
        id: '2efa0617-d155-44dc-9b1e-38efc0dcec02',
        enter: '2019-10-22T03:37:30.804Z',
        exit: '2019-10-22T03:37:30.804Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '849d5bf2-4032-4897-8b30-179ce9d3444b',
        enter: '2019-10-22T03:37:30.804Z',
        exit: '2019-10-22T03:37:30.804Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf862',
    processId: 'flightBooking',
    businessKey: 'T1234FlightBooking02',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      businessKey: 'T1234'
    },
    processName: 'FlightBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: 'COMPLETED',
    serviceUrl: null,
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf823',
      message: 'some thing went wrong'
    },
    addons: [],
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '8ac1fc9d-6de2-4b23-864e-ba79315db317',
        enter: '2019-10-22T03:37:30.804Z',
        exit: '2019-10-22T03:37:30.804Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2efa0617-d155-44dc-9b1e-38efc0dcec02',
        enter: '2019-10-22T03:37:30.804Z',
        exit: '2019-10-22T03:37:30.804Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '849d5bf2-4032-4897-8b30-179ce9d3444b',
        enter: '2019-10-22T03:37:30.804Z',
        exit: '2019-10-22T03:37:30.804Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    processId: 'travels',
    businessKey: 'T1234',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: 'ERROR',
    rootProcessInstanceId: null,
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    error: {
      nodeDefinitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
      message: 'Something went wrong'
    },
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '870bdda0-be04-4e59-bb0b-f9b665eaacc9',
        enter: '2019-10-22T03:37:38.586Z',
        exit: '2019-10-22T03:37:38.586Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '6b4a4fe9-4aab-4e8c-bb79-27b8b6b88d1f',
        enter: '2019-10-22T03:37:30.807Z',
        exit: '2019-10-22T03:37:38.586Z',
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: 'dd33de7c-c39c-484a-83a8-3e1b007fce95',
        enter: '2019-10-22T03:37:30.793Z',
        exit: '2019-10-22T03:37:30.803Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '4',
        name: 'Join',
        definitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
        id: '08c153e8-2766-4675-81f7-29943efdf411',
        enter: '2019-10-22T03:37:30.806Z',
        exit: '2019-10-22T03:37:30.807Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '683cf307-f082-4a8e-9c85-d5a11b13903a',
        enter: '2019-10-22T03:37:30.803Z',
        exit: '2019-10-22T03:37:30.806Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'cf057e58-4113-46c0-be13-6de42ea8377e',
        enter: '2019-10-22T03:37:30.792Z',
        exit: '2019-10-22T03:37:30.803Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: '415a52c0-dc1f-4a93-9238-862dc8072262',
        enter: '2019-10-22T03:37:30.792Z',
        exit: '2019-10-22T03:37:30.792Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: '52d64298-3f28-4aba-a812-dba4077c9665',
        enter: '2019-10-22T03:37:30.79Z',
        exit: '2019-10-22T03:37:30.792Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '6fdee287-08f6-49c2-af2d-2d125ba76ab7',
        enter: '2019-10-22T03:37:30.755Z',
        exit: '2019-10-22T03:37:30.79Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: 'd98c1762-9d3c-4228-9ffc-bc3f423079c0',
        enter: '2019-10-22T03:37:30.753Z',
        exit: '2019-10-22T03:37:30.754Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf862',
        processName: 'FlightBooking',
        businessKey: 'T1234FlightBooking02'
      },
      {
        id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
        processName: 'HotelBooking',
        businessKey: 'T1234HotelBooking01'
      },
      {
        id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863',
        processName: 'FlightBooking',
        businessKey: 'T1234FlightBooking01'
      }
    ]
  },

  {
    id: 'fc1b6535-d557-40df-82c8-b425b9dc531b',
    processId: 'flightBooking',
    businessKey: 'Tra234FlightBooking01',
    parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    parentProcessInstance: {
      id: '538f9feb-5a14-4096-b791-2055b38da7c6',
      processName: 'travels',
      businessKey: 'Tra234'
    },
    processName: 'FlightBooking',
    rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    roles: [],
    state: 'ERROR',
    addons: ['prometheus-monitoring'],
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    error: {
      nodeDefinitionId: '_cq125e139d5-4e77-48c9-84ae-34578e90433n',
      message:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim idest laborum.'
    },
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '18d9e3df-22d2-429c-98d7-f4f2a7b1b471',
        enter: '2019-10-22T03:40:44.086Z',
        exit: '2019-10-22T03:40:44.086Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book flight',
        definitionId: '_cq125e139d5-4e77-48c9-84ae-34578e90433n',
        id: '8a533611-9766-428f-b7ff-78156fc4851d',
        enter: '2019-10-22T03:40:44.086Z',
        exit: '2019-10-22T03:40:44.086Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '2f423120-13ea-4277-97f6-6b7a4b4630d0',
        enter: '2019-10-22T03:40:44.086Z',
        exit: '2019-10-22T03:40:44.086Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: 'ff65b793-bb88-4567-b7e3-73eee35772a4',
    processId: 'hotelBooking',
    businessKey: 'Tra234HotelBooking01',
    parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    parentProcessInstance: {
      id: '538f9feb-5a14-4096-b791-2055b38da7c6',
      processName: 'travels',
      businessKey: 'Tra234'
    },
    rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    processName: 'HotelBooking',
    roles: [],
    state: 'ABORTED',
    addons: ['process-management'],
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578ek1839b',
      message: 'Something went wrong'
    },
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    variables:
      '{"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: 'ed36cd72-5e52-4a53-9d0d-865c98781282',
        enter: '2019-10-22T03:40:44.088Z',
        exit: '2019-10-22T03:40:44.088Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book hotel',
        definitionId: 'ServiceTask_1',
        id: '040cd02a-7f4c-4d41-bda5-4889f82e921f',
        enter: '2019-10-22T03:40:44.088Z',
        exit: '2019-10-22T03:40:44.088Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '8528c7bf-8ac8-401f-b7e5-6f3e69b9f9f2',
        enter: '2019-10-22T03:40:44.088Z',
        exit: '2019-10-22T03:40:44.088Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: '538f9feb-5a14-4096-b791-2055b38da7c6',
    processId: 'travels',
    businessKey: 'Tra234',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    rootProcessInstanceId: null,
    roles: [],
    state: 'ERROR',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    start: '2019-10-22T03:40:44.089Z',
    error: {
      nodeDefinitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
      message: 'Something went wrong'
    },
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Confirm travel',
        definitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
        id: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9',
        enter: '2019-10-22T03:40:44.089Z',
        exit: '2019-10-22T04:42:07.246Z',
        type: 'HumanTaskNode'
      },
      {
        nodeId: '2',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '4cb855b9-e3e4-488d-ae1a-9ea3b8490dba',
        enter: '2019-10-22T03:40:44.086Z',
        exit: '2019-10-22T03:40:44.087Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: '1da9af80-c70e-47b8-9c87-964468fc0b46',
        enter: '2019-10-22T03:40:44.089Z',
        exit: '2019-10-22T03:40:44.089Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: 'f9b90c32-51da-4986-9603-8c800a6b71b1',
        enter: '2019-10-22T03:40:44.087Z',
        exit: '2019-10-22T03:40:44.089Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'f8d7fe9e-0f3e-4919-8fa7-82e0b6821aa9',
        enter: '2019-10-22T03:40:44.085Z',
        exit: '2019-10-22T03:40:44.087Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: '55fd6d56-e4d4-4021-b6c6-02c3c5cb86ce',
        enter: '2019-10-22T03:40:44.085Z',
        exit: '2019-10-22T03:40:44.085Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'fe6b2d9e-6cfd-415a-8e92-5d2be541c3ff',
        enter: '2019-10-22T03:40:44.085Z',
        exit: '2019-10-22T03:40:44.085Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '3bd30bf9-96ba-4f5d-9ed0-981963288418',
        enter: '2019-10-22T03:40:44.07Z',
        exit: '2019-10-22T03:40:44.085Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '739fc473-157d-4b4e-8ad6-e4c28499d24e',
        enter: '2019-10-22T03:40:44.07Z',
        exit: '2019-10-22T03:40:44.07Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'fc1b6535-d557-40df-82c8-b425b9dc531b',
        processName: 'FlightBooking',
        businessKey: 'Tra234FlightBooking01'
      },
      {
        id: 'ff65b793-bb88-4567-b7e3-73eee35772a4',
        processName: 'HotelBooking',
        businessKey: 'Tra234HotelBooking01'
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
    processId: 'travels',
    businessKey: 'Tr1122',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: 'e735128t-6tt7-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    businessKey: null,
    state: 'COMPLETED',
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0bccddee',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    processName: 'travels',
    roles: [],
    state: 'SUSPENDED',
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
    processId: 'flightBooking',
    businessKey: null,
    parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    parentProcessInstance: {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processName: 'travels',
      businessKey: null
    },
    processName: 'FlightBooking',
    rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    roles: [],
    state: 'COMPLETED',
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
        processName: 'FlightBooking test 1',
        businessKey: null
      }
    ]
  },
  {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
    processId: 'flightBooking test1',
    parentProcessInstanceId: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
    parentProcessInstance: {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
      processName: 'FlightBooking',
      businessKey: null
    },
    businessKey: null,
    processName: 'FlightBooking test 1',
    rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    roles: [],
    state: 'SUSPENDED',
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eajabbcc',
        processName: 'FlightBooking test 2',
        businessKey: null
      }
    ]
  },
  {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eajabbcc',
    processId: 'flightBooking test2',
    businessKey: null,
    parentProcessInstanceId: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
    parentProcessInstance: {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
      processName: 'FlightBooking test 1',
      businessKey: null
    },
    rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processName: 'FlightBooking test 2',
    roles: [],
    state: 'COMPLETED',
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
    processId: 'hotelBooking',
    businessKey: null,
    parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    parentProcessInstance: {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processName: 'travels',
      businessKey: null
    },
    rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processName: 'HotelBooking',
    roles: [],
    state: 'COMPLETED',
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring'],
    error: {
      nodeDefinitionId: 'a1qa139d5-4e77-181x8c9-84ae-34578e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    variables:
      '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '7a770672-8493-4566-8288-515c0b5360a8',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book hotel',
        definitionId: 'ServiceTask_1',
        id: 'f10ed686-84f0-48b6-844e-5cfafa32a7bc',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '5a6bd73e-1d3d-43d9-8f27-8081c3014716',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0basadadads',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: 'ABORTED',
    businessKey: null,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88abc',
        processName: 'hotelBooking',
        businessKey: 'Hotel11'
      }
    ]
  },
  {
    id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88abc',
    processId: 'hotelBooking',
    parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0basadadads',
    parentProcessInstance: {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0basadadads',
      processName: 'travels',
      businessKey: null
    },
    rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0basadadads',
    processName: 'HotelBooking',
    businessKey: 'Hotel11',
    roles: [],
    state: 'COMPLETED',
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring'],
    error: {
      nodeDefinitionId: 'a1qa139d5-4e77-181x8c9-84ae-34578e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    variables:
      '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '7a770672-8493-4566-8288-515c0b5360a8',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Book hotel',
        definitionId: 'ServiceTask_1',
        id: 'f10ed686-84f0-48b6-844e-5cfafa32a7bc',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'WorkItemNode'
      },
      {
        nodeId: '3',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '5a6bd73e-1d3d-43d9-8f27-8081c3014716',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
    processId: 'travels',
    businessKey: 'travels001',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b2',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels2',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b3',
    processId: 'travels',
    businessKey: 'Travels@123',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels3',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b4',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels4',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b5',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels5',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b6',
    processId: 'travels',
    businessKey: 'Trav99',
    parentProcessInstance: null,
    parentProcessInstanceId: null,
    processName: 'travels6',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b8',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels8',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b9',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels9',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b10',
    processId: 'travels',
    businessKey: 'newTravels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels10',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b11',
    processId: 'travels',
    businessKey: 'Trav11test',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels11',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b12',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels12',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  },
  {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b13abbccccc',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels13',
    roles: [],
    state: 'COMPLETED',
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000/',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processName: 'FlightBooking',
        businessKey: null
      },
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processName: 'HotelBooking',
        businessKey: null
      }
    ]
  }
];
