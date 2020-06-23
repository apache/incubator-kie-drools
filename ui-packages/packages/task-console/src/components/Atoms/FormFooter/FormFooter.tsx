import React from 'react';
import { ActionGroup, Button } from '@patternfly/react-core';

export interface IFormAction {
  name: string;
  primary?: boolean;
  onActionClick: () => void;
}

interface IOwnProps {
  actions?: IFormAction[];
}

const FormFooter: React.FC<IOwnProps> = ({ actions }) => {
  return actions && actions.length > 0 ? (
    <ActionGroup>
      {actions.map(action => {
        return (
          <Button
            key={'submit-' + action.name}
            type="submit"
            variant={action.primary ? 'primary' : 'secondary'}
            onClick={action.onActionClick}
          >
            {action.name}
          </Button>
        );
      })}
    </ActionGroup>
  ) : null;
};

export default FormFooter;
