package com.spruds.transport.spider.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="jobs")
public class Job implements Serializable {
    @Id
    @Column(name="JOB_ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int jobId;

    private String code;

    @Column(name="BASE_URL")
    private String baseURL;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="DATE_STARTED")
    private Date dateStarted;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="DATE_FINISHED")
    private Date dateFinished;

    @Column(name="IS_ENABLED")
    private boolean enabled;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String toString() {
        return "Job(code=" + code + ")";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
