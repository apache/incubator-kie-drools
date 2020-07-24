import React from 'react';
import { ActionGroup, Button } from '@patternfly/react-core';
import { IFormAction } from '../../../util/uniforms/FormSubmitHandler/FormSubmitHandler';

interface IOwnProps {
  actions?: IFormAction[];
}

const FormFooter: React.FC<IOwnProps> = ({ actions }) => {
  const capitalize = label => {
    return label.charAt(0).toUpperCase() + label.slice(1);
  };

  return actions && actions.length > 0 ? (
    <ActionGroup>
      {actions.map(action => {
        return (
          <Button
            key={'submit-' + action.name}
            type="submit"
            variant={action.primary!== undefined ? (action.primary ? 'primary' : 'secondary') : "primary"}
            onClick={() => {
              if(action.execute) {
                action.execute()
              }
            }}
          >
            {capitalize(action.name)}
          </Button>
        );
      })}
    </ActionGroup>
  ) : null;
};

export default FormFooter;
