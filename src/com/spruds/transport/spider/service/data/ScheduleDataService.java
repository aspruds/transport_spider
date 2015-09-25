package com.spruds.transport.spider.service.data;

import com.spruds.transport.spider.model.Direction;
import com.spruds.transport.spider.model.DirectionStopLink;
import com.spruds.transport.spider.model.FetchResult;
import com.spruds.transport.spider.model.Job;
import com.spruds.transport.spider.model.Route;
import com.spruds.transport.spider.model.Stop;
import com.spruds.transport.spider.model.StopSchedule;
import com.spruds.transport.spider.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ScheduleDataService {

    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public List<Job> getUnfinishedJobs() {
        return hibernateTemplate.find("from Job WHERE dateFinished IS NULL AND enabled=true");
    }

    public boolean isRouteProcessed(Route route) {
        route = getPersistedRoute(route);
        return route.isComplete();
    }

    private Route getPersistedRoute(Route route) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Route.class);
        criteria.add(Expression.eq("routeId", route.getRouteId()));
        criteria.add(Expression.eq("jobId", route.getJobId()));

        Route savedRoute = null;
        List<Route> routes = hibernateTemplate.findByCriteria(criteria);
        if (!routes.isEmpty()) {
            savedRoute = routes.get(0);
        }

        if (savedRoute == null) {
            hibernateTemplate.persist(route);
            return route;
        } else {
            return savedRoute;
        }
    }

    public boolean isDirectionProcessed(Direction direction) {
        direction = getPersistedDirection(direction);
        return direction.isComplete();
    }

    private Direction getPersistedDirection(Direction direction) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Direction.class);
        criteria.add(Expression.eq("directionId", direction.getDirectionId()));
        criteria.add(Expression.eq("jobId", direction.getJobId()));

        Direction savedDirection = null;
        List<Direction> directions = hibernateTemplate.findByCriteria(criteria);
        if (!directions.isEmpty()) {
            savedDirection = directions.get(0);
        }

        if (savedDirection == null) {
            hibernateTemplate.persist(direction);
            savedDirection = direction;
        }

        for (Stop stop : direction.getStops()) {
            stop.setDirection(savedDirection);
            getPersistedStop(stop);
        }

        return savedDirection;
    }

    private Stop getPersistedStop(Stop stop) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Stop.class);
        criteria.add(Expression.eq("stopId", stop.getStopId()));
        criteria.add(Expression.eq("jobId", stop.getJobId()));

        Stop savedStop = null;
        List<Stop> stops = hibernateTemplate.findByCriteria(criteria);
        if (!stops.isEmpty()) {
            savedStop = stops.get(0);
        }

        if (savedStop == null) {
            hibernateTemplate.save(stop);
            savedStop = stop;
        }
        return savedStop;
    }

    public boolean isDirectionStopLinkProcessed(DirectionStopLink dsl) {
        DirectionStopLink persistedLink = getPersistedDirectionStopLink(dsl);
        if (persistedLink != null) {
            return persistedLink.isComplete();
        }
        return false;
    }

    public void markDirectionStopLinkAsProcessed(DirectionStopLink link) {
        DirectionStopLink persistedLink = getPersistedDirectionStopLink(link);
        if (persistedLink != null) {
            persistedLink.setComplete(true);
            hibernateTemplate.update(persistedLink);
        }
    }

    public DirectionStopLink getPersistedDirectionStopLink(DirectionStopLink link) {
        DetachedCriteria criteria = DetachedCriteria.forClass(DirectionStopLink.class);
        criteria.add(Expression.eq("stopId", link.getStopId()));
        criteria.add(Expression.eq("directionId", link.getDirectionId()));
        criteria.add(Expression.eq("jobId", link.getJobId()));

        List<DirectionStopLink> links = hibernateTemplate.findByCriteria(criteria);
        if (!links.isEmpty()) {
            return links.get(0);
        } else {
            hibernateTemplate.save(link);
            return link;
        }
    }

    public void persistStopSchedule(StopSchedule schedule) {
        DetachedCriteria criteria = DetachedCriteria.forClass(StopSchedule.class);
        criteria.add(Expression.eq("stopId", schedule.getStopId()));
        criteria.add(Expression.eq("jobId", schedule.getJobId()));
        criteria.add(Expression.eq("directionId", schedule.getDirectionId()));
        criteria.add(Expression.eq("timingId", schedule.getTimingId()));

        criteria.setProjection(Projections.rowCount());
        Integer count = (Integer)hibernateTemplate.findByCriteria(criteria).get(0);
        if (Integer.valueOf(0).equals(count)) {
            hibernateTemplate.save(schedule);
        }
    }

    public void updateStopCoordinates(Stop stop) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Stop.class);
        criteria.add(Expression.eq("stopId", stop.getStopId()));
        criteria.add(Expression.eq("jobId", stop.getJobId()));

        Stop savedStop = null;
        List<Stop> stops = hibernateTemplate.findByCriteria(criteria);
        if (!stops.isEmpty()) {
            savedStop = stops.get(0);
        }

        if (savedStop != null) {
            savedStop.setLongitude(stop.getLongitude());
            savedStop.setLatitude(stop.getLatitude());
            hibernateTemplate.update(savedStop);
        }
    }

    public void markJobAsStarted(Job job) {
        job.setDateStarted(new Date());
        hibernateTemplate.update(job);
    }

    public void markJobAsFinished(Job job) {
        job.setDateFinished(new Date());
        hibernateTemplate.update(job);
    }

    public void markRouteAsProcessed(Route route) {
        route = getPersistedRoute(route);
        route.setComplete(true);
        hibernateTemplate.update(route);
    }

    public void markDirectionAsProcessed(Direction direction) {
        direction = getPersistedDirection(direction);
        direction.setComplete(true);
        hibernateTemplate.update(direction);
    }

    public void saveFetchResult(String url, String content) {
        if (getFetchResult(url) != null) {
            return;
        }

        FetchResult result = new FetchResult();
        result.setUrl(url);
        result.setStream(content);
        result.setDateFetched(new Date());
        
        hibernateTemplate.save(result);
    }

    public InputStream getFetchResult(String url) {
        InputStream data = null;

        DetachedCriteria criteria = DetachedCriteria.forClass(FetchResult.class);
        criteria.add(Expression.eq("url", url));

        List<FetchResult> results = hibernateTemplate.findByCriteria(criteria);
        if (!results.isEmpty()) {
            try {
            data = IOUtils.convertStringToStream(results.get(0).getStream());
            }
            catch(IOException ex) {
                String message = "could not convert string to stream";
                throw new RuntimeException(message, ex);
            }
        }
        return data;
    }
}
