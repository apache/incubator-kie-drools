import React from 'react';
import { v4 as uuid } from 'uuid';
import { Tooltip } from '@patternfly/react-core';
import './FormattedValue.scss';

type FormattedListProps = {
  valueList: unknown[];
};

const FormattedList = (props: FormattedListProps) => {
  const { valueList } = props;

  if (valueList.length === 0) {
    return (
      <span className="formatted-list formatted-list--no-entries">
        No entries
      </span>
    );
  }
  return (
    <span className="formatted-list">
      {valueList.map((item, index) => (
        <React.Fragment key={uuid()}>
          <FormattedValue value={item} key={uuid()} />
          {index < valueList.length - 1 && (
            <span key={uuid()}>
              ,<br />
            </span>
          )}
        </React.Fragment>
      ))}
    </span>
  );
};

type FormattedValueProps = {
  value: unknown;
  round?: boolean;
};

const FormattedValue = (props: FormattedValueProps) => {
  const { value, round = false } = props;
  let formattedValue;
  let tooltip = null;
  let className = 'formatted-value';
  const tooltipRef = React.useRef();

  switch (typeof value) {
    case 'number':
      if (round && (value.toString().split('.')[1] || []).length > 2) {
        tooltip = value;
        formattedValue = value.toFixed(2);
      } else {
        formattedValue = value;
      }
      break;
    case 'string':
      formattedValue = value;
      break;
    case 'boolean':
      formattedValue = value.toString();
      className += ' formatted-value--capitalize';
      break;
    case 'object':
      if (Array.isArray(value)) {
        formattedValue = <FormattedList valueList={value} />;
      } else if (value === null) {
        formattedValue = <em>Null</em>;
      }
      break;
    default:
      formattedValue = '';
      break;
  }

  return (
    <>
      {tooltip !== null && (
        <Tooltip content={<span>{tooltip}</span>} reference={tooltipRef} />
      )}
      <span
        {...(tooltip !== null ? { ref: tooltipRef } : {})}
        className={className}
      >
        {formattedValue}
      </span>
    </>
  );
};

export default FormattedValue;
