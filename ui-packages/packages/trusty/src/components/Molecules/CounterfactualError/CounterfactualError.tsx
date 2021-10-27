import React, { useState } from 'react';
import { Button, Modal } from '@patternfly/react-core';

const CounterfactualError = () => {
  const [isModalOpen, setIsModalOpen] = useState(true);
  const modalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };
  return (
    <Modal
      isOpen={isModalOpen}
      title="Cannot collect analysis data"
      titleIconVariant="warning"
      showClose={true}
      onClose={modalToggle}
      width={500}
      actions={[
        <Button key="confirm" variant="primary" onClick={modalToggle}>
          Close
        </Button>
      ]}
    >
      <span>
        Something went wrong while running the counterfactual analysis. Try
        again later or contact Customer Support for help.
      </span>
    </Modal>
  );
};

export default CounterfactualError;
