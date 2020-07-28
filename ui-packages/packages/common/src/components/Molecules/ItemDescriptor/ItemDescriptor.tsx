import React from 'react';
import {
  Tooltip,
  Badge,
  TextContent,
  Text,
  TextVariants
} from '@patternfly/react-core';

export interface ItemDescription {
  id: string;
  name: string;
  description?: string;
}

interface IOwnProps {
  itemDescription: ItemDescription;
}

const ItemDescriptor: React.FC<IOwnProps> = ({ itemDescription }) => {
  const idStringModifier = (strId: string) => {
    return (
      <TextContent className="pf-u-display-inline">
        <Text component={TextVariants.small} className="pf-u-display-inline">
          {strId.substring(0, 5)}
        </Text>
      </TextContent>
    );
  };
  return (
    <>
      <Tooltip content={itemDescription.id}>
        <span>
          {itemDescription.name}{' '}
          {itemDescription.description ? (
            <Badge>{itemDescription.description}</Badge>
          ) : (
            idStringModifier(itemDescription.id)
          )}
        </span>
      </Tooltip>
    </>
  );
};

export default ItemDescriptor;