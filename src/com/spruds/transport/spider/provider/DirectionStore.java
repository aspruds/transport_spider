package com.spruds.transport.spider.provider;

import com.spruds.transport.spider.model.Direction;
import com.spruds.transport.spider.model.Job;
import com.spruds.transport.spider.model.Route;
import com.spruds.transport.spider.model.Stop;
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

public class DirectionStore {
    private Logger log = Logger.getLogger(DirectionStore.class);
    private static final String DIRECTION_URL = "?a=p.stops&direction_id=&t=xml&l=en&schedule_id=";
    private HttpManager httpManager;
    
    public List<Direction> fetchDirections(Job job, Route route) throws IOException {
        String url = getDirectionsUrl(job, route.getRouteId());
        HttpGet get = new HttpGet(url);

        HttpResponse response = httpManager.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String message = "error while fetching schedule directions: ";
            log.warn(message +
                    response.getStatusLine().getStatusCode());
            throw new IOException(message);
        }

        HttpEntity entity = response.getEntity();

        List<Direction> directions = parseDirections(route, entity.getContent());
        for(Direction direction: directions) {
            direction.setJobId(job.getJobId());
            
            for(Stop stop: direction.getStops()) {
                stop.setJobId(job.getJobId());
            }
        }
        return directions;
    }

    private String getDirectionsUrl(Job job, int routeId) {
        return job.getBaseURL() + DIRECTION_URL + routeId;
    }

    private List<Direction> parseDirections(Route route, InputStream input) throws IOException {
        List<Direction> directions = new ArrayList<Direction>();

       try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(input));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("direction")) {
                        Direction direction = new Direction();
                        direction.setDirectionId(Integer.valueOf(parser.getAttributeValue(null, "id")));
                        direction.setType(parser.getAttributeValue(null, "type"));
                        direction.setRouteId(route.getRouteId());
                        parseDirection(direction, parser);
                        directions.add(direction);
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException ex) {
            String message = "could not parse schedule by transport type";
            log.error(message);
            throw new RuntimeException(ex);
        }
        return directions;
    }

    private void parseDirection(Direction direction, XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        String name;
        final int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            if ("name".equals(name)) {
                if (parser.next() == XmlPullParser.TEXT) {
                    direction.setName(parser.getText());
                }
            } else if ("stops".equals(name)) {
                List<Stop> stops = new ArrayList<Stop>();
                direction.setStops(stops);
                parseStops(direction, parser);
            }
        }
    }

    private void parseStops(Direction direction, XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        String name;
        final int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            if ("stop".equals(name)) {
                Stop stop = new Stop();
                stop.setDirection(direction);
                stop.setStopId(Integer.valueOf(parser.getAttributeValue(null, "id")));
                if (parser.next() == XmlPullParser.TEXT) {
                    stop.setName(parser.getText());
                }
                direction.getStops().add(stop);
            }
        }
    }

    /**
     * @param httpManager the httpManager to set
     */
    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }
}
