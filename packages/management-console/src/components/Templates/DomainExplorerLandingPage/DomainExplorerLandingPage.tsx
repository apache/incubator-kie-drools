import React from 'react';
import {
  TextContent,
  Text,
  TextVariants,
  PageSection,
  Breadcrumb,
  BreadcrumbItem,
  Title,
  Button,
  EmptyState,
  EmptyStateVariant,
  EmptyStateIcon,
  EmptyStateBody,
  EmptyStateSecondaryActions
} from '@patternfly/react-core';
import { Link } from 'react-router-dom';
import { CubesIcon } from '@patternfly/react-icons';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';

import { useGetQueryFieldsQuery } from '../../../graphql/types';

const DomainExplorerLandingPage = () => {
  const getQuery = useGetQueryFieldsQuery();
  const domains = [];

  const availableDomains =
    !getQuery.loading && getQuery.data.__type.fields.slice(2);

  return (
    <>
      <PageSection variant="light">
        <PageTitleComponent title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>Domain Explorer</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <EmptyState variant={EmptyStateVariant.full}>
          <EmptyStateIcon icon={CubesIcon} />

          {availableDomains.length > 0 ? (
            <>
              <Title headingLevel="h5" size="lg">
                Domains List
              </Title>
              <EmptyStateBody>
                Select a domain from the below list to direct.
              </EmptyStateBody>
              <EmptyStateSecondaryActions>
                {!getQuery.loading &&
                  availableDomains.map((item, index) => {
                    return (
                      <Link to={`/DomainExplorer/${item.name}`} key={index}>
                        <Button variant="link">{item.name}</Button>
                      </Link>
                    );
                  })}
              </EmptyStateSecondaryActions>
            </>
          ) : (
            <TextContent>
              <Text component={TextVariants.h2}>
                No domains available to select
              </Text>
            </TextContent>
          )}
        </EmptyState>
      </PageSection>
    </>
  );
};

export default DomainExplorerLandingPage;
