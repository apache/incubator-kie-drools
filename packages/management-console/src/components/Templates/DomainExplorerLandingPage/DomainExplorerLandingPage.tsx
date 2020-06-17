import React, { useEffect } from 'react';
import {
  PageSection,
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody,
  InjectedOuiaProps,
  withOuiaContext
} from '@patternfly/react-core';
import {
  DomainExplorerListDomains,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/common';
import { Link } from 'react-router-dom';
import PageTitle from '../../Molecules/PageTitle/PageTitle';

const DomainExplorerLandingPage: React.FC<InjectedOuiaProps> = ({
  ouiaContext
}) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'domain-explorer');
  });
  return (
    <>
      <PageSection variant="light">
        <PageTitle title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>Domain Explorer</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Card>
          <CardBody>
            <DomainExplorerListDomains />
          </CardBody>
        </Card>
      </PageSection>
    </>
  );
};

export default withOuiaContext(DomainExplorerLandingPage);
