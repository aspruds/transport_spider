package com.spruds.transport.spider.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="fetch_results")
public class FetchResult {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="FETCH_RESULT_ID")
    private int fetchResultId;

    private String url;
    private String stream;

    @Column(name="DATE_FETCHED")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateFetched;

    public int getFetchResultId() {
        return fetchResultId;
    }

    public void setFetchResultId(int fetchResultId) {
        this.fetchResultId = fetchResultId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateFetched() {
        return dateFetched;
    }

    public void setDateFetched(Date dateFetched) {
        this.dateFetched = dateFetched;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }
}
