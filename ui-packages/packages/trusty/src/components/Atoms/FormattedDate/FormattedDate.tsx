import React from 'react';
import { differenceInDays, format, formatDistanceToNow } from 'date-fns';
import { Tooltip } from '@patternfly/react-core';
import { TooltipProps } from '@patternfly/react-core/dist/js/components/Tooltip/Tooltip';

type FormattedDateProps = {
  date: string;
  preposition?: boolean;
  position?: TooltipProps['position'];
  fullDateAndTime?: boolean;
};

const FormattedDate = (props: FormattedDateProps) => {
  const {
    date,
    preposition = false,
    position = 'auto',
    fullDateAndTime = false
  } = props;
  const difference = differenceInDays(new Date(date), Date.now());
  const fullFormattedDate = format(new Date(date), 'PPpp');
  let formattedDate;

  if (difference === 0) {
    formattedDate = `${formatDistanceToNow(new Date(date))} ago`;
  } else {
    const prefix = preposition ? 'on ' : '';
    formattedDate = `${prefix}${format(new Date(date), 'PP')}`;
  }

  return (
    <>
      {fullDateAndTime ? (
        <span>{fullFormattedDate}</span>
      ) : (
        <Tooltip
          content={fullFormattedDate}
          entryDelay={200}
          exitDelay={100}
          position={position}
        >
          <span>{formattedDate}</span>
        </Tooltip>
      )}
    </>
  );
};

export default FormattedDate;
