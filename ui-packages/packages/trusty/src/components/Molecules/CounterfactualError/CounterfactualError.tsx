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
      title="Counterfactual error"
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
        Something went wrong while running the Counterfactual analysis.
      </span>
    </Modal>
  );
};

export default CounterfactualError;
