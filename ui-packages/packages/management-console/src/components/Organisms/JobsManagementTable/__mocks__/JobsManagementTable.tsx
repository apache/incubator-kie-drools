import React from 'react';

const JobsManagementTable = ({ setSelectedJob }) => {
  const job = {
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
    endpoint: 'http://localhost:4000/jobs',
    expirationTime: '2020-08-27T04:35:54.631Z',
    id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
    lastUpdate: '2020-08-27T03:35:54.635Z',
    priority: 0,
    processId: 'travels',
    processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    repeatInterval: null,
    repeatLimit: null,
    retries: 0,
    rootProcessId: '',
    scheduledId: null,
    status: 'SCHEDULED',
    __typename: 'Job'
  };
  React.useEffect(() => {
    setSelectedJob(job);
  }, []);
  return <></>;
};

export default JobsManagementTable;
