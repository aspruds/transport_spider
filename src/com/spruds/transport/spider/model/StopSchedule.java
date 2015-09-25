package com.spruds.transport.spider.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="stop_schedules")
public class StopSchedule {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="STOP_SCHEDULE_ID")    
    private int stopScheduleId;

    @Column(name="TIMING_ID")
    private int timingId;

    @Column(name="STOP_ID")
    private int stopId;

    @Column(name="DIRECTION_ID")
    private int directionId;
    
    private int hours;

    private int minutes;

    @Column(name="DAYS_VALID")
    private int daysValid;

    @Column(name="IS_LOWFLOOR")
    private boolean lowfloor;

    @Column(name="IS_SHORTENED")
    private boolean shortened;

    @Column(name="IS_CHANGED")
    private boolean changed;

    @Column(name="JOB_ID")
    private int jobId;   
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StopSchedule(id=").append(getStopScheduleId());
        builder.append(",stopId=").append(getStopId());
        builder.append(",directionId=").append(getDirectionId());
        builder.append(",hours=").append(getHours());
        builder.append(",minutes=").append(getMinutes());
        builder.append(",daysValid=").append(getDaysValid());
        builder.append(",lowfloor=").append(isLowfloor());
        builder.append(",shortened=").append(isShortened());
        builder.append(")");
        return builder.toString();
    }

    public int getStopScheduleId() {
        return stopScheduleId;
    }

    public void setStopScheduleId(int stopScheduleId) {
        this.stopScheduleId = stopScheduleId;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getDaysValid() {
        return daysValid;
    }

    public void setDaysValid(int daysValid) {
        this.daysValid = daysValid;
    }

    /**
     * @return the lowfloor
     */
    public boolean isLowfloor() {
        return lowfloor;
    }

    /**
     * @param lowfloor the lowfloor to set
     */
    public void setLowfloor(boolean lowfloor) {
        this.lowfloor = lowfloor;
    }

    /**
     * @return the shortened
     */
    public boolean isShortened() {
        return shortened;
    }

    /**
     * @param shortened the shortened to set
     */
    public void setShortened(boolean shortened) {
        this.shortened = shortened;
    }

    public int getTimingId() {
        return timingId;
    }

    public void setTimingId(int timingId) {
        this.timingId = timingId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public int getDirectionId() {
        return directionId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
