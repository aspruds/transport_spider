package com.spruds.transport.spider.service;

import com.spruds.transport.spider.model.Direction;
import com.spruds.transport.spider.model.DirectionStopLink;
import com.spruds.transport.spider.model.Job;
import com.spruds.transport.spider.model.Route;
import com.spruds.transport.spider.model.Stop;
import com.spruds.transport.spider.model.StopSchedule;
import com.spruds.transport.spider.provider.DirectionStore;
import com.spruds.transport.spider.provider.RouteStore;
import com.spruds.transport.spider.provider.StopLocationStore;
import com.spruds.transport.spider.provider.StopScheduleStore;
import com.spruds.transport.spider.service.data.ScheduleDataService;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpiderService {   
    private Logger log = Logger.getLogger(SpiderService.class);
    private ScheduleDataService scheduleDataService;
    private static RouteStore routeStore;
    private static DirectionStore directionStore;
    private static StopScheduleStore stopScheduleStore;
    private static StopLocationStore stopLocationStore;

    public void start() {
        List<Job> jobs = scheduleDataService.getUnfinishedJobs();
        for(Job job: jobs) {
            log.debug("processing job " + job);
            try {
                processJob(job);
            }
            catch(IOException ex) {
                String message = "job failed";
                throw new RuntimeException(message, ex);
            }
        }
    }

    private void processJob(Job job) throws IOException {
        scheduleDataService.markJobAsStarted(job);
        List<Route> routes = routeStore.fetchRoutes(job);
        for(Route route: routes) {
            if(!scheduleDataService.isRouteProcessed(route)) {
                processRoute(job, route);
                scheduleDataService.markRouteAsProcessed(route);
            }
        }

        // update stop coordinates
        List<Stop> stops = stopLocationStore.fetchStopLocations(job);
        for(Stop stop: stops) {
            scheduleDataService.updateStopCoordinates(stop);
        }
        
        scheduleDataService.markJobAsFinished(job);
    }

    private void processRoute(Job job, Route route) throws IOException {
        List<Direction> directions = directionStore.fetchDirections(job, route);
        for(Direction direction: directions) {
            direction.setRouteId(route.getRouteId());
            
            if(!scheduleDataService.isDirectionProcessed(direction)) {
                processDirection(job, direction);
                scheduleDataService.markDirectionAsProcessed(direction);
            }
        }
    }

    private void processDirection(Job job, Direction direction) throws IOException {
        for(Stop stop: direction.getStops()) {
           DirectionStopLink dsl = new DirectionStopLink();
           dsl.setDirectionId(direction.getDirectionId());
           dsl.setStopId(stop.getStopId());
           dsl.setJobId(direction.getJobId());

           if(!scheduleDataService.isDirectionStopLinkProcessed(dsl)) {
               processStopSchedule(job, stop);
               scheduleDataService.markDirectionStopLinkAsProcessed(dsl);
           }
        }
    }

    private void processStopSchedule(Job job, Stop stop) throws IOException {
        List<StopSchedule> stopSchedule = stopScheduleStore.fetchSchedule(job, stop);
        if(stopSchedule.isEmpty()) {
            throw new RuntimeException("stopSchedule fetch failed");
        }

        for(StopSchedule schedule: stopSchedule) {
            scheduleDataService.persistStopSchedule(schedule);
        }
    }

    public void setRouteStore(RouteStore aRouteStore) {
        routeStore = aRouteStore;
    }

    public void setDirectionStore(DirectionStore aDirectionStore) {
        directionStore = aDirectionStore;
    }

    public void setStopScheduleStore(StopScheduleStore aStopScheduleStore) {
        stopScheduleStore = aStopScheduleStore;
    }

    public void setStopLocationStore(StopLocationStore aStopLocationStore) {
        stopLocationStore = aStopLocationStore;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        SpiderService service = ctx.getBean(SpiderService.class);
        service.start();
    }

    public void setScheduleDataService(ScheduleDataService scheduleDataService) {
        this.scheduleDataService = scheduleDataService;
    }
}
