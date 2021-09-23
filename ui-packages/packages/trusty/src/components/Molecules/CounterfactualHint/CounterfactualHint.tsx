import React, { useState } from 'react';
import {
  Dropdown,
  DropdownItem,
  Hint,
  HintBody,
  HintTitle,
  KebabToggle,
  StackItem
} from '@patternfly/react-core';
import { ExternalLinkAltIcon } from '@patternfly/react-icons';

type CounterfactualHintProps = {
  isVisible: boolean;
};

const CounterfactualHint = (props: CounterfactualHintProps) => {
  const { isVisible } = props;
  const [isDiscarded, setIsDiscarded] = useState(false);
  const [isDropDownOpen, setIsDropDownOpen] = useState(false);

  return (
    <>
      {isVisible && !isDiscarded && (
        <StackItem>
          <Hint
            actions={
              <Dropdown
                toggle={
                  <KebabToggle
                    onToggle={setIsDropDownOpen}
                    id="hint-kebab-toggle"
                  />
                }
                isOpen={isDropDownOpen}
                dropdownItems={[
                  <DropdownItem
                    key="action"
                    id="hint-close"
                    component={
                      <button onClick={() => setIsDiscarded(true)}>
                        Close
                      </button>
                    }
                  />,

                  <DropdownItem
                    key="link"
                    href="https://docs.jboss.org/kogito/release/1.4.0/html_single/#con-audit-console_kogito-dmn-models"
                    target="_blank"
                    icon={<ExternalLinkAltIcon />}
                  >
                    Learn More
                  </DropdownItem>
                ]}
                position="right"
                isPlain
              />
            }
          >
            <HintTitle>Create a counterfactual</HintTitle>
            <HintBody>
              Select a desired counterfactual Outcome; one or more Data Types,
              and modify the input constraints.
            </HintBody>
          </Hint>
        </StackItem>
      )}
    </>
  );
};

export default CounterfactualHint;
