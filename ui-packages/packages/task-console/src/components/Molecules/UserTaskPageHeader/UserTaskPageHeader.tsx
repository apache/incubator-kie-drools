import React from 'react';
import { PageSection } from '@patternfly/react-core';

import PageTitle from '../PageTitle/PageTitle';

const UserTaskPageHeader: React.FC = () => {
  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle title="Task Inbox" />
      </PageSection>
    </React.Fragment>
  );
};

export default UserTaskPageHeader;
