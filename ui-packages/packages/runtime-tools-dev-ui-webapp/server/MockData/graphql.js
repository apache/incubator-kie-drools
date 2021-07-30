module.exports ={ ProcessInstanceData : [
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
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
        name: 'Manager decision',
        status: 'COMPLETED'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
        name: 'Milestone 1: Order placed',
        status: 'ACTIVE'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: 'AVAILABLE'
      },
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
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75ze6',
        name: 'Manager decision',
        status: 'COMPLETED'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75a56',
        name: 'Milestone 1: Order placed',
        status: 'ACTIVE'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75q76',
        name: 'Milestone 2: Order shipped',
        status: 'AVAILABLE'
      },
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
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d751g6',
        name: 'Manager decision',
        status: 'COMPLETED'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75v36',
        name: 'Milestone 1: Order placed',
        status: 'ACTIVE'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75o96',
        name: 'Milestone 2: Order shipped',
        status: 'AVAILABLE'
      },
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
    serviceUrl: "http://localhost:4000",
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
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m26',
        name: 'Manager decision',
        status: 'COMPLETED'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75uu26',
        name: 'Milestone 1: Order placed',
        status: 'ACTIVE'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75mi86',
        name: 'Milestone 2: Order shipped',
        status: 'AVAILABLE'
      },
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
    id: 'tEE12-fo54-l665-mp112-akou112345566',
    processId: 'travels',
    businessKey: 'TEE12',
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
    serviceUrl: 'http://localhost:4000',
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
    milestones: [],
    childProcessInstances: []
  },
  {
    id: 'RZ11-tu77-hj321-bnfhe1-xdr2134',
    processId: 'travels',
    businessKey: 'MPTQ',
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
    serviceUrl: 'http://localhost:4000',
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
    milestones: [],
    childProcessInstances: []
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
    milestones: [],
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
    milestones: [],
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
    milestones: [],
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
    milestones: [],
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
    serviceUrl: 'http://localhost:4000',
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
        name: 'Confirm travel not found',
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
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
        name: 'Manager decision',
        status: 'COMPLETED'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
        name: 'Milestone 1: Order placed',
        status: 'ACTIVE'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: 'AVAILABLE'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m88',
        name: 'Milestone 3: Order delivered and closed with customer sign off',
        status: 'ACTIVE'
      },
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
    milestones: [],
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
    milestones: [],
    childProcessInstances: []
  },
  {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
    processId: 'flightBooking',
    businessKey: "Trrr",
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
    milestones: [],
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
    milestones: [],
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
    businessKey: "TP444",
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
    milestones: [],
    childProcessInstances: []
  },
  {
    id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
    processId: 'hotelBooking',
    businessKey: "TM111",
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
    milestones: [],
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
    businessKey: 'TL111',
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
    milestones: [],
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
    milestones: [],
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
    serviceUrl: 'http://localhost:4000',
    rootProcessInstanceId: null,
    serviceUrl:'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
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
    milestones: [],
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
    businessKey: 'Tp111',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels2',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
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
    milestones: [],
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
    serviceUrl: 'http://localhost:4000/',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
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
    milestones: [],
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
    businessKey: "TTTTT",
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels4',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    serviceUrl:null,
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
    milestones: [],
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
    businessKey: 'Tr11111111',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels5',
    roles: [],
    state: 'ACTIVE',
    rootProcessInstanceId: null,
    serviceUrl:'http://localhost:4000',
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
    milestones: [],
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
    serviceUrl:null,
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
    milestones: [],
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
    serviceUrl:null,
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
    milestones: [],
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
    serviceUrl:'http://localhost:4000',
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
    milestones: [],
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
    serviceUrl:'http://localhost:4000',
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
    milestones: [],
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
    serviceUrl: 'http://localhost:4000',
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
    milestones: [],
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
    serviceUrl:null,
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
    milestones: [],
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
    serviceUrl: null,
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
    milestones: [],
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
],
JobsData:[
  {
    id: "6e74a570-31c8-4020-bd70-19be2cb625f3_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: null,
    status: "EXECUTED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: "0",
    retries: 1,
    lastUpdate: "2020-08-27T03:35:50.147Z",
    expirationTime: null,
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
    executionCounter: 2
  },
  {
    id: "dad3aa88-5c1e-4858-a919-6123c675a0fa_1",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "SCHEDULED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 3,
    lastUpdate: "2020-07-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
    executionCounter: 6
  },
  {
    id: "2234dde-npce1-2908-b3131-6123c675a0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "CANCELED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 2,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
    executionCounter: 0
  },
  {
    id: "T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "ERROR",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: 30300,
    repeatLimit: 3,
    scheduledId: null,
    retries: 7,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
    executionCounter: 3
  },
  {
    id: "bff4ee-11qw23-6675-po987-qwedfrt45a0fa_5",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "RETRY",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 15,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: 'b2761011-3043-4f48-82bd-1395bf651a91',
    executionCounter: 2
  },
  {
    id: "eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "SCHEDULED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 2,
    lastUpdate: "2020-06-29T03:35:54.635Z",
    expirationTime: "2020-08-29T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: null,
    executionCounter: 0
  },
  {
    id: "dad3aa88-5c1e-4858-a919-uey23c675a0fa_0",
    processId: "travels",
    processInstanceId: "e4448857-fa0c-403b-ad69-f0a353458b9d",
    rootProcessId: "",
    status: "SCHEDULED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 5,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: '08c153e8-2766-4675-81f7-29943efdf411',
    executionCounter: 0
  },
  {
    id: "6e74a570-31c8-4020-bd70-o413be2cb625f3_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: null,
    status: "EXECUTED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: "0",
    retries: 0,
    lastUpdate: "2020-08-27T03:35:50.147Z",
    expirationTime: null,
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
    executionCounter: 5
  },
  {
    id: "dad3aa88-5c1e-4858-a919-61ai21c675a0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "SCHEDULED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 0,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
    executionCounter: 7
  },
  {
    id: "2234dde-npce1-2908-b3131-i15333c675a0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "CANCELED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 0,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
    executionCounter: 3
  },
  {
    id: "T3113e-vbg43-2234-lo89-u8103214ra0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "ERROR",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: 30300,
    repeatLimit: 3,
    scheduledId: null,
    retries: 0,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
    executionCounter: 2
  },
  {
    id: "bff4ee-11qw23-6675-po987-qwedfrt45a0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "RETRY",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 0,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: 'b2761011-3043-4f48-82bd-1395bf651a91',
    executionCounter: 9
  },
  {
    id: "eff4ee-11qw23-6675-pokau97-ql10s5ut45a0fa_0",
    processId: "travels",
    processInstanceId: "8035b580-6ae4-4aa8-9ec0-e18e19809e0b",
    rootProcessId: "",
    status: "SCHEDULED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 0,
    lastUpdate: "2020-08-29T03:35:54.635Z",
    expirationTime: "2020-08-29T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: null,
    executionCounter: 1
  },
  {
    id: "dad3aa88-5c1e-4858-a919-781ns75a0fa_0",
    processId: "travels",
    processInstanceId: "e4448857-fa0c-403b-ad69-f0a353458b9d",
    rootProcessId: "",
    status: "SCHEDULED",
    priority: 0,
    callbackEndpoint: "http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0",
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 0,
    lastUpdate: "2020-08-27T03:35:54.635Z",
    expirationTime: "2020-08-27T04:35:54.631Z",
    endpoint: 'http://localhost:4000/jobs',
    nodeInstanceId: '08c153e8-2766-4675-81f7-29943efdf411',
    executionCounter: 8
  }
],
UserTaskInstances: [
  {
    id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
    description: null,
    name: 'VisaApplication',
    priority: '1',
    processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: 'admin',
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-02-19T11:11:56.282Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
    outputs: '{}',
    referenceName: 'Apply for visa (Empty Form)',
    lastUpdate: '2020-02-19T11:11:56.282Z',
    endpoint:
      'http://localhost:4000/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3'
  },
  {
    id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
    description: '',
    name: 'ConfirmTravel',
    priority: '1',
    processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: 'paulo',
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-02-19T10:59:34.185Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
    outputs: '{}',
    referenceName: 'Confirm travel (Submit fails)',
    lastUpdate: '2020-02-19T13:22:40.909Z',
    endpoint:
      'http://localhost:4000/travels/9ae407dd-cdfa-4722-8a49-0a6d2e14550d/VisaApplication/047ec38d-5d57-4330-8c8d-9bd67b53a529'
  },
  {
    id: 'f6be5b6b-34de-4b06-b6e7-05bcf8ba7f54',
    description: '',
    name: 'ConfirmTravel',
    priority: '1',
    processInstanceId: '4bfdd404-c46a-4751-b401-b1428a30fa07',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Completed',
    actualOwner: 'paulo',
    adminGroups: [],
    adminUsers: [],
    completed: '2020-02-19T10:49:24.623Z',
    started: '2020-02-19T10:49:16.559Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
    outputs: '{}',
    referenceName: 'Confirm travel',
    endpoint:
      'http://localhost:4000/travels/4bfdd404-c46a-4751-b401-b1428a30fa07/ConfirmTravel/f6be5b6b-34de-4b06-b6e7-05bcf8ba7f54'
  },
  {
    id: '5cead49f-7649-410a-89ff-840cc52adf52',
    description: '',
    name: 'ConfirmTravel',
    priority: '1',
    processInstanceId: '7e31993d-8c9a-45e8-997d-7156632a520f',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Aborted',
    actualOwner: 'john',
    adminGroups: [],
    adminUsers: [],
    completed: '2020-02-19T09:55:52.574Z',
    started: '2020-02-19T09:55:38.81Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
    outputs: '{}',
    referenceName: 'Confirm travel (Details Error)',
    endpoint:
      'http://localhost:4000/travels/7e31993d-8c9a-45e8-997d-7156632a520f/ConfirmTravel/5cead49f-7649-410a-89ff-840cc52adf52'
  },
  {
    id: '841b9dba-3d91-4725-9de3-f9f4853b417e',
    name: 'VisaApplication',
    referenceName: 'Apply for visa (Submit fail)',
    description: null,
    priority: '1',
    processInstanceId: 'a84df9ba-f41e-47cb-9aa5-67cffff2c5bc',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: 'john',
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:02.386Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"trip":{"city":"New York","country":"US","begin":"2019-12-09T23:00:00.000+01:00","end":"2019-12-14T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","Priority":"1","Skippable":"true","traveller":{"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","address":{"street":"polna","city":"Krakow","zipCode":"32000","country":"Poland"}},"GroupId":"group1"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:02.39Z',
    endpoint:
      'http://localhost:4000/travels/a84df9ba-f41e-47cb-9aa5-67cffff2c5bc/VisaApplication/841b9dba-3d91-4725-9de3-f9f4853b417e'
  },
  {
    id: '475e3eb3-1de4-4f68-a146-79c236353a03',
    name: 'VisaApplication',
    referenceName: 'Apply for visa (No Form)',
    description: null,
    priority: '1',
    processInstanceId: '5c10ec86-4cf5-4de2-a5ab-d962893f079d',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:04.88Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: ['john', 'poul'],
    inputs:
      '{"trip":{"city":"New York","country":"US","begin":"2019-12-09T23:00:00.000+01:00","end":"2019-12-14T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","Priority":"1","Skippable":"true","traveller":{"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","address":{"street":"polna","city":"Krakow","zipCode":"32000","country":"Poland"}},"GroupId":"group1"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:04.884Z',
    endpoint:
      'http://localhost:4000/travels/5c10ec86-4cf5-4de2-a5ab-d962893f079d/VisaApplication/475e3eb3-1de4-4f68-a146-79c236353a03'
  },
  {
    id: 'c6fedd33-8fea-4adf-97a0-9d2b6676e9d0',
    name: 'VisaApplication',
    referenceName: 'Apply for visa',
    description: null,
    priority: '1',
    processInstanceId: '9d19c8ec-aa5c-4681-9bb5-ff8cbce08091',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:07.075Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"trip":{"city":"New York","country":"US","begin":"2019-12-09T23:00:00.000+01:00","end":"2019-12-14T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","Priority":"1","Skippable":"true","traveller":{"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","address":{"street":"polna","city":"Krakow","zipCode":"32000","country":"Poland"}},"GroupId":"group1"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:07.079Z',
    endpoint:
      'http://localhost:4000/travels/9d19c8ec-aa5c-4681-9bb5-ff8cbce08091/VisaApplication/c6fedd33-8fea-4adf-97a0-9d2b6676e9d0'
  },
  {
    id: '809aae9e-f0bf-4892-b0c9-4be80664d2aa',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel (Empty Form)',
    description: null,
    priority: '1',
    processInstanceId: '5204b2d2-54ec-4f07-8f8c-3079a1f5fe9b',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:15.381Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:15.389Z',
    endpoint:
      'http://localhost:4000/travels/5204b2d2-54ec-4f07-8f8c-3079a1f5fe9b/ConfirmTravel/809aae9e-f0bf-4892-b0c9-4be80664d2aa'
  },
  {
    id: '615b9143-1468-4028-b454-6122e2139f5c',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel (No Form)',
    description: null,
    priority: '1',
    processInstanceId: 'd6685e24-0aad-4e5c-a64f-29e95cae9e5e',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:18.044Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:18.05Z',
    endpoint:
      'http://localhost:4000/travels/d6685e24-0aad-4e5c-a64f-29e95cae9e5e/ConfirmTravel/615b9143-1468-4028-b454-6122e2139f5c'
  },
  {
    id: '2e37c623-a535-4eb1-ae5b-6eaf7f4039c3',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: '40ab14f0-3a10-4ffd-96e1-05b0028943b4',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:20.832Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:20.835Z',
    endpoint:
      'http://localhost:4000/travels/40ab14f0-3a10-4ffd-96e1-05b0028943b4/ConfirmTravel/2e37c623-a535-4eb1-ae5b-6eaf7f4039c3'
  },
  {
    id: '3c1d6da4-436a-4728-bc24-9a69781bcbac',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: 'e1d4b174-a9b5-465b-b142-018df18d87d8',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:23.698Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:00:23.701Z',
    endpoint:
      'http://localhost:4000/travels/e1d4b174-a9b5-465b-b142-018df18d87d8/ConfirmTravel/3c1d6da4-436a-4728-bc24-9a69781bcbac'
  },
  {
    id: '86ddb2c7-c8e1-435f-a274-a8b0eb066ac1',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: 'b8eebfe7-45f4-4ce7-9019-1740222b302a',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:01:12.65Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:01:12.655Z',
    endpoint:
      'http://localhost:4000/travels/b8eebfe7-45f4-4ce7-9019-1740222b302a/ConfirmTravel/86ddb2c7-c8e1-435f-a274-a8b0eb066ac1'
  },
  {
    id: '9e1e0601-f7bd-4ad3-88ca-57afc9e3cf9d',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: '0c469bbf-988b-44e0-8fc9-90286500c519',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:01:14.412Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:01:14.416Z',
    endpoint:
      'http://localhost:4000/travels/0c469bbf-988b-44e0-8fc9-90286500c519/ConfirmTravel/9e1e0601-f7bd-4ad3-88ca-57afc9e3cf9d'
  },
  {
    id: 'a3eefa9a-51b0-4820-bc99-94c370389ed5',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: '718f44c2-e574-482c-9a24-60d17e474dde',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:01:16.421Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:01:16.426Z',
    endpoint:
      'http://localhost:4000/travels/718f44c2-e574-482c-9a24-60d17e474dde/ConfirmTravel/a3eefa9a-51b0-4820-bc99-94c370389ed5'
  },
  {
    id: 'aa9f477c-5172-4913-956a-6c76f7278207',
    name: 'VisaApplication',
    referenceName: 'Apply for visa',
    description: '',
    priority: '1',
    processInstanceId: 'fe523245-05e2-4a0c-abf7-a774cfe9d3f9',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Completed',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: '2020-07-30T09:22:16.417Z',
    started: '2020-07-30T08:59:55.64Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"trip":{"city":"New York","country":"US","begin":"2019-12-09T23:00:00.000+01:00","end":"2019-12-14T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","Priority":"1","Skippable":"true","traveller":{"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","address":{"street":"polna","city":"Krakow","zipCode":"32000","country":"Poland"}},"GroupId":"group1"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:22:16.446Z',
    endpoint:
      'http://localhost:4000/travels/fe523245-05e2-4a0c-abf7-a774cfe9d3f9/VisaApplication/aa9f477c-5172-4913-956a-6c76f7278207'
  },
  {
    id: '99bb167f-144a-42fb-8f40-b80f34f5bed9',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: '',
    priority: '1',
    processInstanceId: '28f63147-b948-4a63-acce-ec2c5c5f15ca',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Aborted',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T08:59:44.749Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:32:14.556Z',
    endpoint:
      'http://localhost:4000/travels/28f63147-b948-4a63-acce-ec2c5c5f15ca/ConfirmTravel/99bb167f-144a-42fb-8f40-b80f34f5bed9'
  },
  {
    id: '2f8e0452-b50d-40f8-a657-b32c812828ef',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: '',
    priority: '1',
    processInstanceId: '53923218-2a54-40c5-8b01-872d8dd2ec67',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Completed',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: '2020-07-30T09:40:26.896Z',
    started: '2020-07-30T08:59:47.779Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:40:26.901Z',
    endpoint:
      'http://localhost:4000/travels/53923218-2a54-40c5-8b01-872d8dd2ec67/ConfirmTravel/2f8e0452-b50d-40f8-a657-b32c812828ef'
  },
  {
    id: '5ac50f25-192c-4719-9847-f9b8bdfe3381',
    name: 'VisaApplication',
    referenceName: 'Apply for visa',
    description: '',
    priority: '1',
    processInstanceId: '8e0eb71c-b5a3-44a3-9b82-d786781a6598',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Completed',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: '2020-07-30T09:43:29.625Z',
    started: '2020-07-30T08:59:58.565Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"trip":{"city":"New York","country":"US","begin":"2019-12-09T23:00:00.000+01:00","end":"2019-12-14T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","Priority":"1","Skippable":"true","traveller":{"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","address":{"street":"polna","city":"Krakow","zipCode":"32000","country":"Poland"}},"GroupId":"group1"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:43:29.628Z',
    endpoint:
      'http://localhost:4000/travels/8e0eb71c-b5a3-44a3-9b82-d786781a6598/VisaApplication/5ac50f25-192c-4719-9847-f9b8bdfe3381'
  },
  {
    id: 'e878bca4-84f6-46a3-a864-9632ab490cab',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: '',
    priority: '1',
    processInstanceId: 'a413fc40-b192-4879-94aa-dc84d0394f67',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Aborted',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T09:00:13.404Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T09:44:00.082Z',
    endpoint:
      'http://localhost:4000/travels/a413fc40-b192-4879-94aa-dc84d0394f67/ConfirmTravel/e878bca4-84f6-46a3-a864-9632ab490cab'
  },
  {
    id: '61676d8f-56b7-4bc3-bd40-4c905d4ab176',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: '90a4d7db-c41f-4e01-b6b1-201be823bc07',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T12:57:33.309Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T12:57:33.318Z',
    endpoint:
      'http://localhost:4000/travels/90a4d7db-c41f-4e01-b6b1-201be823bc07/ConfirmTravel/61676d8f-56b7-4bc3-bd40-4c905d4ab176'
  },
  {
    id: '0a65028e-c02b-41f9-9260-5b703fb27a27',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: 'fdc72f2c-41fe-440e-bbf6-22510fa4766d',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T13:05:52.696Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T13:05:52.699Z',
    endpoint:
      'http://localhost:4000/travels/fdc72f2c-41fe-440e-bbf6-22510fa4766d/ConfirmTravel/0a65028e-c02b-41f9-9260-5b703fb27a27'
  },
  {
    id: '5fe852de-8d00-4197-9936-3842c648fee1',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: 'b4096227-2c8a-463f-a7a9-776027f77bf4',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T13:08:20.509Z',
    excludedUsers: [],
    potentialGroups: ['employees', 'interns', 'managers'],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T13:23:26.824Z',
    endpoint:
      'http://localhost:4000/travels/b4096227-2c8a-463f-a7a9-776027f77bf4/ConfirmTravel/5fe852de-8d00-4197-9936-3842c648fee1'
  },
  {
    id: '61676d8f-56b7-4bc3-bd40-4c905d4ab1762',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: '90a4d7db-c41f-4e01-b6b1-201be823bc07',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: 'mary',
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T12:57:33.309Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T12:57:33.318Z',
    endpoint:
      'http://localhost:4000/travels/90a4d7db-c41f-4e01-b6b1-201be823bc07/ConfirmTravel/61676d8f-56b7-4bc3-bd40-4c905d4ab1762'
  },
  {
    id: '0a65028e-c02b-41f9-9260-5b703fb27a271',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: 'fdc72f2c-41fe-440e-bbf6-22510fa4766d',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: 'mary',
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T13:05:52.696Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T13:05:52.699Z',
    endpoint:
      'http://localhost:4000/travels/fdc72f2c-41fe-440e-bbf6-22510fa4766d/ConfirmTravel/0a65028e-c02b-41f9-9260-5b703fb27a271'
  },
  {
    id: '5fe852de-8d00-4197-9936-3842c648fee134',
    name: 'ConfirmTravel',
    referenceName: 'Confirm travel',
    description: null,
    priority: '1',
    processInstanceId: 'b4096227-2c8a-463f-a7a9-776027f77bf4',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: 'mary',
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-07-30T13:08:20.509Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"GroupId":"group2"}',
    outputs: '{}',
    lastUpdate: '2020-07-30T13:23:26.824Z',
    endpoint:
      'http://localhost:4000/travels/b4096227-2c8a-463f-a7a9-776027f77bf4/ConfirmTravel/5fe852de-8d00-4197-9936-3842c648fee134'
  }
]
}