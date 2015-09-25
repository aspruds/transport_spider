package com.spruds.transport.spider.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="routes")
public class Route implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(name="ROUTE_ID")
    private int routeId;

    @Column(name="TRANSPORT_TYPE_ID")
    private TransportType transportType;

    private String number;
    
    private String name;
    
    @Transient
    private List<Direction> directions;
    
    @Column(name="IS_LOWFLOOR")
    private boolean lowfloor;
    
    @Column(name="IS_COMPLETE")
    private boolean complete;

    @Column(name="JOB_ID")
    private int jobId;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Route(id=").append(routeId);
        builder.append(",transportType=").append(transportType);
        builder.append(",number=").append(number);
        builder.append(",name=").append(name);
        builder.append(")");
        return builder.toString();
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
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
}
