import React, { useEffect, useRef } from 'react';
import { InputGroup, TextInput } from '@patternfly/react-core';
import flatpickr from 'flatpickr';
import { Instance as flatpickrInstance } from 'flatpickr/dist/types/instance';
import leftArrow from '../../../static/images/leftArrow.svg';
import rightArrow from '../../../static/images/rightArrow.svg';
import './DatePicker.scss';

type DatePickerProps = {
  fromDate?: string;
  id: string;
  label?: string;
  minDate?: string;
  maxDate?: string;
  onDateUpdate: (date: string) => void;
  value?: string;
};

const DatePicker = (props: DatePickerProps) => {
  const { fromDate, minDate, maxDate, value, onDateUpdate, id, label } = props;
  const datePicker = useRef<HTMLInputElement>();

  useEffect(() => {
    let calendar: flatpickrInstance;
    const onChange = (selectedDates: Date[], dateStr: string) => {
      onDateUpdate(dateStr);
    };
    if (datePicker.current) {
      calendar = flatpickr(datePicker.current, {
        allowInput: true,
        altInput: true,
        altFormat: 'F j, Y',
        dateFormat: 'Y-m-d',
        defaultDate: value,
        minDate,
        maxDate,
        monthSelectorType: 'static',
        onChange,
        prevArrow: `<img alt="Prev" src="${leftArrow}">`,
        nextArrow: `<img alt="Next" src="${rightArrow}">`,
        static: true
      });
    }
    return () => {
      calendar.destroy();
    };
  }, [fromDate, maxDate, onDateUpdate, minDate, value, id]);

  return (
    <InputGroup>
      <TextInput
        name={id}
        id={id}
        type="date"
        aria-label={label}
        ref={datePicker}
      />
    </InputGroup>
  );
};

export default DatePicker;

export const datePickerSetup = () => {
  flatpickr.l10ns.en.weekdays.shorthand.forEach((day, index, daysArray) => {
    if (daysArray[index] === 'Thu' || daysArray[index] === 'Th') {
      daysArray[index] = 'Th';
    } else if (daysArray[index] === 'Sat' || daysArray[index] === 'Sa') {
      daysArray[index] = 'Sa';
    } else {
      daysArray[index] = daysArray[index].charAt(0);
    }
  });
};
