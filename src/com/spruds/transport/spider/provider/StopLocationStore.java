package com.spruds.transport.spider.provider;

import com.spruds.transport.spider.model.Job;
import com.spruds.transport.spider.model.Stop;
import com.spruds.transport.spider.utils.HttpManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

public class StopLocationStore {
    private Logger log = Logger.getLogger(StopLocationStore.class);
    private static final String STOP_LOCATION_URL = "cache/stops.txt";
    private HttpManager httpManager;

    private String getUrl(Job job) {
        return job.getBaseURL() + STOP_LOCATION_URL;
    }
    
    public List<Stop> fetchStopLocations(final Job job) throws IOException {
        String url = getUrl(job);
        HttpGet get = new HttpGet(url);

        HttpResponse response = httpManager.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String message = "error fetching stop location: ";
            log.warn(message +
                    response.getStatusLine().getStatusCode());
            throw new IOException(message);
        }

        HttpEntity entity = response.getEntity();
        List<Stop> stops = new ArrayList<Stop>();
        try {
            InputStream in = entity.getContent();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            String line = null;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                Integer stopId = Integer.valueOf(parts[0]);
                Double latitude = Double.valueOf(parts[2]);
                Double longitude = Double.valueOf(parts[3]);

                Stop stop = new Stop();
                stop.setStopId(stopId);
                stop.setLatitude(latitude);
                stop.setLongitude(longitude);
                stop.setJobId(job.getJobId());

                stops.add(stop);
            }
        }
        catch(IOException ex) {
            throw new RuntimeException(ex);
        }

        return stops;
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }
}
