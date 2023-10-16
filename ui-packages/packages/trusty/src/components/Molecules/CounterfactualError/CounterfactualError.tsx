/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
