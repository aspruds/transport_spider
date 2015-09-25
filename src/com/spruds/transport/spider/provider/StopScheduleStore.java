package com.spruds.transport.spider.provider;

import com.spruds.transport.spider.model.Job;
import com.spruds.transport.spider.model.Stop;
import com.spruds.transport.spider.model.StopSchedule;
import com.spruds.transport.spider.utils.HttpManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class StopScheduleStore {
    private Logger log = Logger.getLogger(StopScheduleStore.class);
    private static final String TAG = "StopScheduleStore";
    private static final String STOP_SCHEDULE_URL = "?a=p.schedule&t=xml&l=en&direction_id=";
    private HttpManager httpManager;
    
    public List<StopSchedule> fetchSchedule(Job job, Stop stop) throws IOException {
        String url = getScheduleUrl(job, stop);
        HttpGet get = new HttpGet(url);

        HttpResponse response = httpManager.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String message = "error fetching stop schedule: ";
            log.warn(message +
                    response.getStatusLine().getStatusCode());
            throw new IOException(message);
        }

        HttpEntity entity = response.getEntity();
        List<StopSchedule> stopSchedule = parseSchedule(stop, entity.getContent());
        for(StopSchedule schedule: stopSchedule) {
            schedule.setJobId(job.getJobId());
        }
        return stopSchedule;
    }

    private String getScheduleUrl(Job job, Stop stop) {
        String url = job.getBaseURL() + STOP_SCHEDULE_URL
                + stop.getDirection().getDirectionId();
        
        url = url + "&stop_id=" + stop.getStopId();
        return url;
    }

    private List<StopSchedule> parseSchedule(Stop stop, InputStream input) throws IOException {
        List<StopSchedule> stopSchedules = new ArrayList<StopSchedule>();

       try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(input));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("day")) {
                        int daysValid = Integer.parseInt(parser.getAttributeValue(null, "day"));
                        parseHours(stopSchedules, stop, daysValid, parser);
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException ex) {
            String message = "could not parse schedule by transport type";
            log.error(message);
            throw new RuntimeException(ex);
        }
        return stopSchedules;
    }

    private void parseHours(List<StopSchedule> stopSchedules, Stop stop,
            int daysValid, XmlPullParser parser) throws XmlPullParserException, IOException {
        
        int type;
        String name;
        final int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            if ("hour".equals(name)) {
                int hours = Integer.valueOf(parser.getAttributeValue(null, "hr"));
                parseMinutes(stopSchedules, stop, daysValid, hours, parser);
            }
        }
    }

    private void parseMinutes(List<StopSchedule> stopSchedules, Stop stop,
            int daysValid, int hours, XmlPullParser parser)
            throws XmlPullParserException, IOException {
        
        int type;
        String name;
        final int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            if ("minutes".equals(name)) {
                StopSchedule schedule = new StopSchedule();
                schedule.setTimingId(Integer.valueOf(parser.getAttributeValue(null, "id")));

                String changed = parser.getAttributeValue(null, "changed");
                if(changed != null) {
                    schedule.setChanged(true);
                }

                String shortened = parser.getAttributeValue(null, "shortened");
                if(shortened != null) {
                    schedule.setShortened(true);
                }

                String lowfloor = parser.getAttributeValue(null, "lowfloor");
                if(lowfloor != null) {
                    schedule.setLowfloor(true);
                }               
                
                if (parser.next() == XmlPullParser.TEXT) {
                    schedule.setStopId(stop.getStopId());
                    schedule.setDirectionId(stop.getDirection().getDirectionId());
                    schedule.setDaysValid(daysValid);
                    schedule.setHours(hours);
                    schedule.setMinutes(Integer.valueOf(parser.getText()));

                    stopSchedules.add(schedule);
                }
            }
        }
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }
}
