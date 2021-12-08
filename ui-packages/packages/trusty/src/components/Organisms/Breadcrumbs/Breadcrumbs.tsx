import React from 'react';
import useBreadcrumbs from 'use-react-router-breadcrumbs';
import { Breadcrumb, BreadcrumbItem } from '@patternfly/react-core';
import { RouteComponentProps } from 'react-router-dom';
import ExecutionId from '../../Atoms/ExecutionId/ExecutionId';
import TrustyLink from '../../Atoms/TrustyLink/TrustyLink';

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
                key={match.url}
                isActive={location.pathname === match.url}
                render={({ className }) => (
                  <TrustyLink url={match.url} className={className}>
                    {breadcrumb}
                  </TrustyLink>
                )}
              />
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
    <span>
      Execution <ExecutionId id={match.params.id} />
    </span>
  );
};

const routes = [
  { path: '/audit', breadcrumb: 'Audit investigation' },
  {
    path: '/audit/:executionType/:id/outcomes-details',
    breadcrumb: 'Outcomes details'
  },
  // the following route is needed to display a dedicated breadcrumb path for executions with only 1 outcome
  {
    path: '/audit/:executionType/:id/single-outcome',
    breadcrumb: 'Outcome'
  },
  {
    path: '/audit/:executionType/:id/model-lookup',
    breadcrumb: 'Model lookup'
  },
  {
    path: '/audit/:executionType/:id/input-data',
    breadcrumb: 'Input data'
  },
  {
    path: '/audit/:executionType/:id/counterfactual-analysis',
    breadcrumb: 'Counterfactual analysis'
  },
  {
    path: '/audit/:executionType/:id',
    breadcrumb: AuditDetailBreadcrumb
  }
];

const excludePaths = ['/', '/audit/:executionType'];

export default Breadcrumbs;
