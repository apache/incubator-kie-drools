import React from 'react';
import {
  TextContent,
  Text,
  TextVariants,
  Title,
  Button,
  EmptyState,
  EmptyStateVariant,
  EmptyStateIcon,
  EmptyStateBody,
  EmptyStateSecondaryActions
} from '@patternfly/react-core';
import { CubesIcon } from '@patternfly/react-icons';
import { Link } from 'react-router-dom';
import { GraphQL } from '../../../graphql/types';
import useGetQueryFieldsQuery = GraphQL.useGetQueryFieldsQuery;
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

const DomainExplorerListDomains: React.FC<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  const getQuery = useGetQueryFieldsQuery();
  let availableDomains =
    !getQuery.loading && getQuery.data.__type.fields.slice(2);

  availableDomains =
    availableDomains &&
    availableDomains.filter((item) => {
      /* istanbul ignore else */
      if (item.name !== 'Jobs') {
        return item;
      }
    });
  return (
    <EmptyState
      variant={EmptyStateVariant.full}
      {...componentOuiaProps(ouiaId, 'domain-explorer-list-domains', ouiaSafe)}
    >
      <EmptyStateIcon icon={CubesIcon} />

      {availableDomains.length > 0 ? (
        <>
          <Title headingLevel="h5" size="lg">
            Domains List
          </Title>
          <EmptyStateBody>Select a domain below</EmptyStateBody>
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
          <Text component={TextVariants.h2}>No domains available</Text>
        </TextContent>
      )}
    </EmptyState>
  );
};

export default DomainExplorerListDomains;
