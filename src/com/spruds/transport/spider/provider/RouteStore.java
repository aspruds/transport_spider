package com.spruds.transport.spider.provider;


import com.spruds.transport.spider.model.Job;
import com.spruds.transport.spider.model.Route;
import com.spruds.transport.spider.model.TransportType;
import com.spruds.transport.spider.utils.HttpManager;
import com.spruds.transport.spider.utils.ThreadUtils;
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

public class RouteStore {
    private Logger log = Logger.getLogger(RouteStore.class);
    private static final String ROUTE_URL = "?a=p.routes&t=xml&l=en&transport_id=";
    private static final int REQUEST_DELAY = 2500;
    private HttpManager httpManager;

    public List<Route> fetchRoutes(Job job) throws IOException {
        List<Route> routes = new ArrayList<Route>();
        routes.addAll(fetchRoutesByTransportType(job, TransportType.BUS));
        routes.addAll(fetchRoutesByTransportType(job, TransportType.TROLLEYBUS));
        routes.addAll(fetchRoutesByTransportType(job, TransportType.TRAM));
        routes.addAll(fetchRoutesByTransportType(job, TransportType.MINIBUS));
        
        return routes;
    }

    private List<Route> fetchRoutesByTransportType(Job job, TransportType transportType) throws IOException {
        String url = getRouteUrl(job, transportType);
        HttpGet get = new HttpGet(url);

        HttpResponse response = httpManager.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String message = "error while fetching scedule by transport type: ";
            log.warn(message +
                    response.getStatusLine().getStatusCode());
            throw new IOException(message);
        }

        HttpEntity entity = response.getEntity();
        
        List<Route> routes = parseRoutes(transportType, entity.getContent());
        for(Route route: routes) {
            route.setJobId(job.getJobId());
        }
        return routes;
    }

    private String getRouteUrl(Job job, TransportType type) {
        return job.getBaseURL() + ROUTE_URL + type.name().toLowerCase();
    }
    
    private List<Route> parseRoutes(TransportType transportType, InputStream input) throws IOException {
        List<Route> routes = new ArrayList<Route>();

       try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(input));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("route")) {
                        Route route = parseRoute(parser);
                        route.setTransportType(transportType);
                        routes.add(route);
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException ex) {
            String message = "could not parse schedule by transport type";
            log.error(message);
            throw new RuntimeException(ex);
        }
        return routes;
    }

    private Route parseRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        Route route = new Route();
        int type;
        String name;
        final int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            if ("number".equals(name)) {
                if (parser.next() == XmlPullParser.TEXT) {
                    route.setNumber(parser.getText());
                }
            } else if ("direction".equals(name)) {
                if (parser.next() == XmlPullParser.TEXT) {
                    route.setName(parser.getText());
                }
            } else if ("schedule_id".equals(name)) {
                if (parser.next() == XmlPullParser.TEXT) {
                    route.setRouteId(Integer.parseInt(parser.getText()));
                }
            } else if ("lowfloor".equals(name)) {
                if (parser.next() == XmlPullParser.TEXT) {
                    if("1".equals(parser.getText())) {
                        route.setLowfloor(true);
                    }
                }
            }
        }
        return route;
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }
}
