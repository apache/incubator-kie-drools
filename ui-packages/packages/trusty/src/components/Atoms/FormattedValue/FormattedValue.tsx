import React from 'react';
import { v4 as uuid } from 'uuid';
import './FormattedValue.scss';

type FormattedListProps = {
  valueList: any[];
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
};

const FormattedValue = (props: FormattedValueProps) => {
  const { value } = props;
  let formattedValue;
  let className = 'formatted-value';

  switch (typeof value) {
    case 'number':
    case 'bigint':
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
      }
      break;
    default:
      formattedValue = '';
      break;
  }

  return <span className={className}>{formattedValue}</span>;
};

export default FormattedValue;
