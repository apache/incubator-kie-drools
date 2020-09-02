import React from 'react';
import JobsPanelDetailsModal from '../JobsPanelDetailsModal';
import { GraphQL, getWrapper } from '@kogito-apps/common';
import { InfoCircleIcon } from '@patternfly/react-icons';

const props = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: GraphQL.JobStatus.Executed,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: '2020-08-27T03:35:50.147Z',
    expirationTime: '2020-08-27T03:35:50.147Z'
  },
  modalTitle: (
    <>
      <InfoCircleIcon
        className="pf-u-mr-sm"
        color="var(--pf-global--info-color--100)"
      />
      {'Jobs Details'}
    </>
  ),
  isModalOpen: true,
  handleModalToggle: jest.fn()
};
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
describe('Job details modal tests', () => {
  it('Snapshot testing', () => {
    const wrapper = getWrapper(
      <JobsPanelDetailsModal {...props} />,
      'JobsPanelDetailsModal'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
