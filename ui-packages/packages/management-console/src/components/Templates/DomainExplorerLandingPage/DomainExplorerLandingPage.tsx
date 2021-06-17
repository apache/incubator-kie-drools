import React, { useEffect } from 'react';
import {
  PageSection,
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody
} from '@patternfly/react-core';
import { DomainExplorerListDomains } from '@kogito-apps/common';
import {
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
  OUIAProps
} from '@kogito-apps/ouia-tools';
import { Link } from 'react-router-dom';
import PageTitle from '../../Molecules/PageTitle/PageTitle';

const DomainExplorerLandingPage: React.FC<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('domain-explorer');
  });
  return (
    <div {...componentOuiaProps(ouiaId, 'DomainExplorerLandingPage', ouiaSafe)}>
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
    </div>
  );
};

export default DomainExplorerLandingPage;
