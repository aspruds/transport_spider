package com.spruds.transport.spider.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name="directions_stops")
public class DirectionStopLink implements Serializable {
    @Id
    @Column(name="DIRECTION_STOP_ID")
    private int directionStopId;

    @NaturalId
    @Column(name="DIRECTION_ID")
    private int directionId;

    @NaturalId
    @Column(name="STOP_ID")
    private int stopId;
    
    @Column(name="IS_COMPLETE")
    private boolean complete;

    @Column(name="JOB_ID")
    private int jobId;   

    public int getDirectionId() {
        return directionId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getDirectionStopId() {
        return directionStopId;
    }

    public void setDirectionStopId(int directionStopId) {
        this.directionStopId = directionStopId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
