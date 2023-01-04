import React, { useEffect } from 'react';
import {
  PageSection,
  Breadcrumb,
  BreadcrumbItem
} from '@patternfly/react-core';
import { DomainExplorer } from '@kogito-apps/common';
import {
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
  OUIAProps
} from '@kogito-apps/ouia-tools';
import { Link } from 'react-router-dom';
import { Redirect, RouteComponentProps, StaticContext } from 'react-router';
import './DomainExplorerPage.css';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import * as H from 'history';

interface IOwnProps {
  domains: string[];
  loadingState: boolean;
}

interface MatchProps {
  domainName?: string;
}

export type LocationProps = H.LocationState & {
  parameters?: Record<string, unknown>[];
  selected?: string[];
  finalFilters?: Record<string, unknown>;
  filterChips?: string[];
};

const DomainExplorerPage: React.FC<
  IOwnProps &
    RouteComponentProps<MatchProps, StaticContext, LocationProps> &
    OUIAProps
> = ({ ouiaId, ouiaSafe, ...props }) => {
  const rememberedParams =
    (props.location.state && props.location.state.parameters) || [];
  const rememberedSelections =
    (props.location.state && props.location.state.selected) || [];
  const rememberedFilters: Record<string, unknown> =
    (props.location.state && props.location.state.finalFilters) || {};
  const rememberedChips =
    (props.location.state && props.location.state.filterChips) || [];
  const domainName = props.match.params.domainName;
  let BreadCrumb = props.location.pathname.split('/');
  BreadCrumb = BreadCrumb.filter((item) => {
    if (item !== '') {
      return item;
    }
  });
  const [pathName] = BreadCrumb.slice(-1);

  const metaData = {
    metadata: [
      {
        processInstances: [
          'id',
          'processName',
          'state',
          'start',
          'lastUpdate',
          'businessKey',
          'serviceUrl'
        ]
      }
    ]
  };

  const defaultChip = ['metadata / processInstances / state: ACTIVE'];

  const defaultFilter = {
    metadata: {
      processInstances: {
        state: {
          equal: 'ACTIVE'
        }
      }
    }
  };

  useEffect(() => {
    return ouiaPageTypeAndObjectId('domain-explorer', domainName);
  });
  return (
    <div {...componentOuiaProps(ouiaId, 'DataExplorerPage', ouiaSafe)}>
      {!props.loadingState &&
        !props.domains.includes(domainName) &&
        !props.domains.includes(pathName) && (
          <Redirect
            to={{
              pathname: '/NoData',
              state: {
                prev: location.pathname,
                title: 'Domain not found',
                description: `Domain with the name ${domainName} not found`,
                buttonText: 'Go to domain explorer'
              }
            }}
          />
        )}
      <PageSection variant="light">
        <PageTitle title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          {BreadCrumb.map((item, index) => {
            if (index === BreadCrumb.length - 1) {
              return (
                <BreadcrumbItem isActive key={index}>
                  {item}
                </BreadcrumbItem>
              );
            } else {
              return (
                <BreadcrumbItem key={index}>
                  <Link to={'/DomainExplorer'}>
                    {item.replace(/([A-Z])/g, ' $1').trim()}
                  </Link>
                </BreadcrumbItem>
              );
            }
          })}
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <DomainExplorer
          rememberedParams={rememberedParams}
          rememberedSelections={rememberedSelections}
          rememberedFilters={rememberedFilters}
          rememberedChips={rememberedChips}
          domainName={domainName}
          metaData={metaData}
          defaultChip={defaultChip}
          defaultFilter={defaultFilter}
        />
      </PageSection>
    </div>
  );
};

export default React.memo(DomainExplorerPage);
