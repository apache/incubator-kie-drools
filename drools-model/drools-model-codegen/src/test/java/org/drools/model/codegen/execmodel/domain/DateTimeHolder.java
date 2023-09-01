package org.drools.model.codegen.execmodel.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeHolder {

    private ZonedDateTime zonedDateTime;

    public DateTimeHolder(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return zonedDateTime.toLocalDateTime();
    }

    public LocalDate getLocalDate() {
        return zonedDateTime.toLocalDate();
    }

    public Date getDate() {
        return Date.from(zonedDateTime.toInstant());
    }
}
