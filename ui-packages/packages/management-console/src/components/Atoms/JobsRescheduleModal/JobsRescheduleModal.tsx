import React from 'react';
import {
  Modal,
  ModalVariant,
  Button,
  ModalBoxBody,
  Form,
  FormGroup,
  TextInput,
  TextContent,
  Text
} from '@patternfly/react-core';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { OutlinedClockIcon } from '@patternfly/react-icons';
import DateTimePicker from 'react-datetime-picker';
import { handleJobReschedule, setTitle } from '../../../utils/Utils';
import { refetchContext } from '../../contexts';
import './JobsRescheduleModal.css';

interface IOwnProps {
  actionType: string;
  modalTitle: JSX.Element;
  isModalOpen: boolean;
  handleModalToggle: () => void;
  modalAction: JSX.Element[];
  job: GraphQL.Job;
  rescheduleClicked?: boolean;
  setRescheduleClicked?: (rescheduleClicked: boolean) => void;
}

const JobsRescheduleModal: React.FC<IOwnProps & OUIAProps> = ({
  job,
  rescheduleClicked,
  setRescheduleClicked,
  actionType,
  modalAction,
  modalTitle,
  isModalOpen,
  handleModalToggle,
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

  const [errorMessage, setErrorMessage] = React.useState<string>('');

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
  const refetch = React.useContext(refetchContext);

  const onApplyReschedule = async (): Promise<any> => {
    await handleJobReschedule(
      job,
      repeatInterval,
      repeatLimit,
      rescheduleClicked,
      setErrorMessage,
      setRescheduleClicked,
      scheduleDate,
      refetch
    );
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
      <ModalBoxBody className="kogito-management-console--jobsModal__ModalBody">
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
              className="kogito-management-console--jobsModal__TimeNow"
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
      onClick={handleErrorModal}
    >
      OK
    </Button>
  ];

  React.useEffect(() => {
    errorMessage.length > 0 && handleErrorModal();
  }, [errorMessage]);

  const errorString: string = `Reschedule of job ${job.id} failed. Message: ${errorMessage}`;

  const errorModalContent = (): JSX.Element => {
    return (
      <ModalBoxBody>
        <TextContent>
          <Text>{errorString}</Text>
        </TextContent>
      </ModalBoxBody>
    );
  };
  return (
    <>
      <Modal
        variant={ModalVariant.small}
        aria-labelledby={actionType + ' modal'}
        aria-label={actionType + ' modal'}
        title=""
        header={modalTitle}
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
