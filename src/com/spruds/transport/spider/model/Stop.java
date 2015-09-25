package com.spruds.transport.spider.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="stops")
public class Stop implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(name="STOP_ID")
    private int stopId;

    @Transient
    private Direction direction;

    private Double longitude;
    private Double latitude;
    private String name;

    @Column(name="JOB_ID")
    private int jobId;   
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Stop(id=").append(getStopId());
        builder.append(",directionId=").append(getDirection().getDirectionId());
        builder.append(",longitude=").append(getLongitude());
        builder.append(",latitude=").append(getLatitude());
        builder.append(",name=").append(getName());
        builder.append(")");
        return builder.toString();
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
