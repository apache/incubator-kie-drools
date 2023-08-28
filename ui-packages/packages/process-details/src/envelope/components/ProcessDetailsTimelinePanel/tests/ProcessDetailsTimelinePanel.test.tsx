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

import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import ProcessDetailsTimelinePanel from '../ProcessDetailsTimelinePanel';
import {
  JobStatus,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared/dist/types';
import { act } from 'react-dom/test-utils';
import TestProcessDetailsDriver from '../../../tests/mocks/TestProcessDetailsDriver';
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

const MockedIcon = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons/dist/js/icons/user-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    UserIcon: () => {
      return <MockedIcon />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/js/icons/check-circle-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    CheckCircleIcon: () => {
      return <MockedIcon />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/js/icons/error-circle-o-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    ErrorCircleOIcon: () => {
      return <MockedIcon />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/js/icons/on-running-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    OnRunningIcon: () => {
      return <MockedIcon />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/js/icons/outlined-clock-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    OutlinedClockIcon: () => {
      return <MockedIcon />;
    }
  })
);

jest.mock(
  '@kogito-apps/management-console-shared/dist/components/JobsRescheduleModal/JobsRescheduleModal',
  () =>
    Object.assign(
      {},
      jest.requireActual('@kogito-apps/management-console-shared'),
      {
        JobsRescheduleModal: () => {
          return <></>;
        }
      }
    )
);

const driver = new TestProcessDetailsDriver(
  '2d962eef-45b8-48a9-ad4e-9cde0ad6af89'
);

const props1 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    serviceUrl: 'http://localhost:4000',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '111-555-898',
        name: 'Confirm travel',
        definitionId: 'abc-efg-hij',
        id: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9r25e',
        enter: new Date('2019-10-22T03:40:44.089Z'),
        exit: new Date('2019-10-22T04:43:01.144Z'),
        type: 'Join'
      },
      {
        nodeId: '111-555-898',
        name: 'Confirm travel',
        definitionId: 'abc-efg-hij',
        id: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9',
        enter: new Date('2019-10-22T03:40:44.089Z'),
        exit: new Date('2019-10-22T04:43:01.144Z'),
        type: 'HumanTaskNode'
      },
      {
        name: 'End Event 1',
        definitionId: '_7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: new Date('2019-10-22T04:43:01.144Z'),
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000',
      nodeInstanceId: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9'
    },
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Scheduled,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000',
      nodeInstanceId: '2f588da5-a323-4111-9017-3093ef9319d1'
    }
  ],
  driver,
  omittedProcessTimelineEvents: []
};

const props2 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000'
    }
  ],
  driver,
  omittedProcessTimelineEvents: []
};

const props3 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Completed,
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Jobs',
        definitionId: 'StartJob_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0111111',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000'
    },
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0111111',
      processId: 'travels1111',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Scheduled,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000',
      nodeInstanceId: ''
    }
  ],
  driver,
  omittedProcessTimelineEvents: ['StartProcess']
};

const props4 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Completed,
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [],
  driver,
  omittedProcessTimelineEvents: []
};

describe('ProcessDetailsTimelinePanel component tests', () => {
  it('Snapshot testing for service url available', () => {
    const container = render(<ProcessDetailsTimelinePanel {...props1} />);
    expect(container).toMatchSnapshot();
  });

  it('Snapshot testing for no service url', () => {
    const container = render(<ProcessDetailsTimelinePanel {...props2} />);
    expect(container).toMatchSnapshot();
  });

  it('Snapshot testing for completed state', () => {
    const container = render(<ProcessDetailsTimelinePanel {...props3} />);
    expect(container).toMatchSnapshot();
  });

  it('skip test success', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleSkipMock = jest.spyOn(driver, 'handleProcessSkip');
    driverHandleSkipMock.mockResolvedValue();
    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-0'));
    });
    await waitFor(() => screen.getAllByText('Skip'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('skip'));
    });

    expect(driverHandleSkipMock).toHaveBeenCalled();
  });

  it('skip test failure', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleSkipMock = jest.spyOn(driver, 'handleProcessSkip');
    driverHandleSkipMock.mockRejectedValue('Error');
    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-0'));
    });
    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-0'));
    });
    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-0'));
    });
    await waitFor(() => screen.getAllByText('Skip'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('skip'));
    });

    expect(driverHandleSkipMock).toHaveBeenCalled();
  });

  it('retry test success', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleRetryMock = jest.spyOn(driver, 'handleProcessRetry');
    driverHandleRetryMock.mockResolvedValue();
    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-0'));
    });
    await waitFor(() => screen.getAllByText('Retry'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('retry'));
    });

    expect(driverHandleRetryMock).toHaveBeenCalled();
  });

  it('retry test failure', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleRetryMock = jest.spyOn(driver, 'handleProcessRetry');
    driverHandleRetryMock.mockRejectedValue('Error');
    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-0'));
    });
    await waitFor(() => screen.getAllByText('Retry'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('retry'));
    });

    expect(driverHandleRetryMock).toHaveBeenCalled();
  });

  it('retrigger test success', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleNodeInstanceRetrigger = jest.spyOn(
      driver,
      'handleNodeInstanceRetrigger'
    );
    driverHandleNodeInstanceRetrigger.mockResolvedValue();

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });
    await waitFor(() => screen.getAllByText('Retrigger node'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('retrigger'));
    });

    expect(driverHandleNodeInstanceRetrigger).toHaveBeenCalled();
  });

  it('retrigger test failure', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleNodeInstanceRetrigger = jest.spyOn(
      driver,
      'handleNodeInstanceRetrigger'
    );
    driverHandleNodeInstanceRetrigger.mockRejectedValue('Error');

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });
    await waitFor(() => screen.getAllByText('Retrigger node'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('retrigger'));
    });

    expect(driverHandleNodeInstanceRetrigger).toHaveBeenCalled();
  });

  it('cancel test success', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleNodeInstanceCancel = jest.spyOn(
      driver,
      'handleNodeInstanceCancel'
    );
    driverHandleNodeInstanceCancel.mockResolvedValue();

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });
    await waitFor(() => screen.getAllByText('Cancel node'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('cancel'));
    });

    expect(driverHandleNodeInstanceCancel).toHaveBeenCalled();
  });

  it('cancel test failure', async () => {
    render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverHandleNodeInstanceCancel = jest.spyOn(
      driver,
      'handleNodeInstanceCancel'
    );
    driverHandleNodeInstanceCancel.mockRejectedValue('Error');

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });
    await waitFor(() => screen.getAllByText('Cancel node'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('cancel'));
    });

    expect(driverHandleNodeInstanceCancel).toHaveBeenCalled();
  });

  it('job details click', async () => {
    const container = render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverJobsQuery = jest.spyOn(driver, 'jobsQuery');
    driverJobsQuery.mockResolvedValue(new Promise((r) => r([])));

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });

    await waitFor(() => screen.getAllByText('Job Details'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('job-details'));
    });

    expect(container).toMatchSnapshot();
  });

  it('job reschedule', async () => {
    const container = render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverRescheduleJob = jest.spyOn(driver, 'rescheduleJob');
    driverRescheduleJob.mockResolvedValue(
      new Promise((r) =>
        r({
          modalTitle: 'Mock Reschedule',
          modalContent: 'success'
        })
      )
    );

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });
    await waitFor(() => screen.getAllByText('Job Reschedule'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('job-reschedule'));
    });

    expect(container).toMatchSnapshot();
  });

  it('job cancel', async () => {
    const container = render(<ProcessDetailsTimelinePanel {...props1} />);
    const driverCancelJob = jest.spyOn(driver, 'cancelJob');
    driverCancelJob.mockResolvedValue(
      new Promise((r) =>
        r({
          modalTitle: 'Mock Cancel',
          modalContent: 'success'
        })
      )
    );

    await act(async () => {
      fireEvent.click(screen.getByTestId('timeline-kebab-toggle-2'));
    });
    await waitFor(() => screen.getAllByText('Job Cancel'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('job-cancel'));
    });
    expect(container).toMatchSnapshot();
  });
});
