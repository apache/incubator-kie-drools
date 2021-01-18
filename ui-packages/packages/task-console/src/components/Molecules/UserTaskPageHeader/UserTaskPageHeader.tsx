import React from 'react';
import { PageSection } from '@patternfly/react-core';

import PageTitle from '../PageTitle/PageTitle';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/common';

const UserTaskPageHeader: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  return (
    <React.Fragment>
      <PageSection
        variant="light"
        {...componentOuiaProps(ouiaId, 'user-task-page-header', ouiaSafe)}
      >
        <PageTitle title="Task Inbox" />
      </PageSection>
    </React.Fragment>
  );
};

export default UserTaskPageHeader;
