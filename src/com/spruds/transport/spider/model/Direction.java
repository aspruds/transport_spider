package com.spruds.transport.spider.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="directions")
public class Direction implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    @Column(name="DIRECTION_ID")
    private int directionId;

    @Column(name="ROUTE_ID")    
    private int routeId;

    private String type;

    private String name;

    @Transient
    private List<Stop> stops;
    
    @Column(name="IS_COMPLETE")
    private boolean complete;

    @Column(name="JOB_ID")
    private int jobId;   
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Direction(id=").append(directionId);
        builder.append(",routeId=").append(getRouteId());
        builder.append(",type=").append(type);
        builder.append(",name=").append(name);
        builder.append(")");
        return builder.toString();
    }

    public int getDirectionId() {
        return directionId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
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

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
}
