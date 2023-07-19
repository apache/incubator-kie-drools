/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import {
  Modal,
  ModalVariant,
  ModalBoxBody
} from '@patternfly/react-core/dist/js/components/Modal';
import {
  TextContent,
  Text
} from '@patternfly/react-core/dist/js/components/Text';
import {
  Form,
  FormGroup
} from '@patternfly/react-core/dist/js/components/Form';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { TextInput } from '@patternfly/react-core/dist/js/components/TextInput';
import { Job } from '../../types';
import { OutlinedClockIcon } from '@patternfly/react-icons/dist/js/icons/outlined-clock-icon';
import DateTimePicker from 'react-datetime-picker';
import { setTitle } from '../../utils/Utils';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import '../styles.css';

interface IOwnProps {
  actionType: string;
  isModalOpen: boolean;
  handleModalToggle: () => void;
  modalAction: JSX.Element[];
  job: Job;
  rescheduleError: string;
  setRescheduleError: (rescheduleError: string) => void;
  handleJobReschedule: any;
}

const JobsRescheduleModal: React.FC<IOwnProps & OUIAProps> = ({
  job,
  actionType,
  modalAction,
  isModalOpen,
  handleModalToggle,
  rescheduleError,
  setRescheduleError,
  handleJobReschedule,
  ouiaId,
  ouiaSafe
}) => {
  const [scheduleDate, setScheduleDate] = React.useState<Date>(
    new Date(job.expirationTime)
  );
  const [repeatInterval, setRepeatInterval] = React.useState<number | string>(
    job.repeatInterval
  );
  const [repeatLimit, setRepeatLimit] = React.useState<number | string>(
    job.repeatLimit
  );
  const [errorModalOpen, setErrorModalOpen] = React.useState<boolean>(false);

  const handleIntervalChange = (value: number | string): void => {
    setRepeatInterval(value);
  };

  const handleLimitChange = (value: number | string): void => {
    setRepeatLimit(value);
  };

  const handleDateChange = (value: Date): void => {
    setScheduleDate(value);
  };

  const handleTimeNow = (): void => {
    setScheduleDate(new Date());
  };

  const onApplyReschedule = async (): Promise<void> => {
    await handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate);
  };

  const applyAction: JSX.Element[] = [
    <Button
      key="apply-selection"
      variant="primary"
      id="apply-button"
      onClick={onApplyReschedule}
    >
      Apply
    </Button>
  ];
  const modalContent = (): JSX.Element => {
    return (
      <ModalBoxBody className="kogito-management-console-shared--jobsModal__ModalBody">
        <Form isHorizontal>
          <FormGroup label="Expiration Time" fieldId="horizontal-form-name">
            {scheduleDate && scheduleDate !== undefined && (
              <DateTimePicker
                value={scheduleDate}
                minDate={new Date()}
                onChange={handleDateChange}
              />
            )}
            <Button
              className="kogito-management-console-shared--jobsModal__TimeNow"
              id="Time-now"
              onClick={handleTimeNow}
            >
              <OutlinedClockIcon /> Now
            </Button>
          </FormGroup>
          <FormGroup
            label="Repeat Interval"
            fieldId="repeat-interval"
            helperText={
              repeatInterval === null
                ? 'Input disabled since it is an one-time run job'
                : null
            }
          >
            <TextInput
              type="text"
              id="repeat-interval-input"
              name="repeat-interval-input"
              aria-describedby="repeat-interval"
              value={repeatInterval || ''}
              onChange={handleIntervalChange}
              isDisabled={repeatInterval === null}
            />
          </FormGroup>
          <FormGroup
            label="Repeat Limit"
            fieldId="repeat-limit"
            helperText={
              repeatLimit === null
                ? 'Input disabled since it is an one-time run job'
                : null
            }
          >
            <TextInput
              type="text"
              id="repeat-limit-input"
              name="repeat-limit-input"
              aria-describedby="repeat-limit"
              value={repeatLimit || ''}
              onChange={handleLimitChange}
              isDisabled={repeatLimit === null}
            />
          </FormGroup>
        </Form>
      </ModalBoxBody>
    );
  };
  const handleErrorModal = (): void => {
    setErrorModalOpen(!errorModalOpen);
  };

  const errorModalAction: JSX.Element[] = [
    <Button
      key="confirm-selection"
      variant="primary"
      onClick={() => {
        handleErrorModal();
        setRescheduleError('');
      }}
    >
      OK
    </Button>
  ];

  React.useEffect(() => {
    rescheduleError.length > 0 && handleErrorModal();
  }, [rescheduleError]);

  const errorModalContent = (): JSX.Element => {
    return (
      <ModalBoxBody>
        <TextContent>
          <Text>{rescheduleError}</Text>
        </TextContent>
      </ModalBoxBody>
    );
  };
  return (
    <>
      <Modal
        variant={ModalVariant.small}
        aria-labelledby={'actionType' + ' modal'}
        aria-label={actionType + ' modal'}
        title=""
        header={setTitle('success', 'Job Reschedule')}
        isOpen={isModalOpen}
        onClose={handleModalToggle}
        actions={[...applyAction, ...modalAction]}
        {...componentOuiaProps(ouiaId, 'job-reschedule-modal', ouiaSafe)}
      >
        {modalContent()}
      </Modal>
      <Modal
        variant={ModalVariant.small}
        aria-labelledby={'Reschedule error modal'}
        aria-label={'Reschedule error modal'}
        title=""
        header={setTitle('failure', 'Job Reschedule')}
        isOpen={errorModalOpen}
        onClose={handleErrorModal}
        actions={errorModalAction}
        {...componentOuiaProps(ouiaId, 'job-reschedule-error-modal', ouiaSafe)}
      >
        {errorModalContent()}
      </Modal>
    </>
  );
};

export default JobsRescheduleModal;
