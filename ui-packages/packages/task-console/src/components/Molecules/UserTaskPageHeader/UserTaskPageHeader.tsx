import React from 'react';
import { Link } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  PageSection
} from '@patternfly/react-core';

import PageTitle from '../PageTitle/PageTitle';

const UserTaskPageHeader: React.FC = () => {
  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle title="User Tasks" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>User Tasks</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
    </React.Fragment>
  );
};

export default UserTaskPageHeader;
