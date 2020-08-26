import React from 'react';
import useBreadcrumbs from 'use-react-router-breadcrumbs';
import { Breadcrumb, BreadcrumbItem } from '@patternfly/react-core';
import { RouteComponentProps } from 'react-router-dom';

const Breadcrumbs = () => {
  const breadcrumbs = useBreadcrumbs(routes, { excludePaths });

  return (
    <>
      {breadcrumbs.length >= 2 && (
        <Breadcrumb>
          {breadcrumbs.map(({ match, location, breadcrumb }) => {
            return (
              <BreadcrumbItem
                className="breadcrumb-item"
                to={match.url}
                key={match.url}
                isActive={location.pathname === match.url}
              >
                {breadcrumb}
              </BreadcrumbItem>
            );
          })}
        </Breadcrumb>
      )}
    </>
  );
};

type executionIdParam = {
  id: string;
};

interface AuditDetailBreadcrumbProps
  extends RouteComponentProps<executionIdParam> {}

const AuditDetailBreadcrumb = (props: AuditDetailBreadcrumbProps) => {
  const { match } = props;
  return (
    <span style={{ textTransform: 'uppercase' }}>ID #{match.params.id}</span>
  );
};

const routes = [
  { path: '/audit', breadcrumb: 'Audit Investigation' },
  {
    path: '/audit/:executionType/:id/outcomes-details',
    breadcrumb: 'Outcomes Details'
  },
  // the following route is needed to display a dedicated breadcrumb path for executions with only 1 outcome
  { path: '/audit/:executionType/:id/single-outcome', breadcrumb: 'Outcome' },
  {
    path: '/audit/:executionType/:id/model-lookup',
    breadcrumb: 'Model Lookup'
  },
  { path: '/audit/:executionType/:id/input-data', breadcrumb: 'Input Data' },
  { path: '/audit/:executionType/:id', breadcrumb: AuditDetailBreadcrumb }
];

const excludePaths = ['/', '/audit/:executionType'];

export default Breadcrumbs;
