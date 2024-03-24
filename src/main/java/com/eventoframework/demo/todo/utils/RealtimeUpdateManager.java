package com.eventoframework.demo.todo.utils;

import com.evento.common.utils.ProjectorStatus;
import org.springframework.data.repository.CrudRepository;

public class RealtimeUpdateManager<T extends RealtimeResource, ID> {

    protected final CrudRepository<T, ID> repository;
    private final RealtimeUpdatesService realtimeService;


    public RealtimeUpdateManager(CrudRepository<T, ID> repository, RealtimeUpdatesService realtimeService) {
        this.repository = repository;
        this.realtimeService = realtimeService;
    }

    public void save(T resource, ProjectorStatus projectorStatus) {
        var o = repository.save(resource);
        realtimeService.notifyCreate(o, projectorStatus);
    }

    public void update(T resource, ProjectorStatus projectorStatus) {
        var o = repository.save(resource);
        notifyUpdate(o,projectorStatus);
    }

    public void delete(T resource, ProjectorStatus projectorStatus) {
        repository.delete(resource);
        realtimeService.notifyDelete(resource, projectorStatus);
    }

    public void notifyUpdate(T resource, ProjectorStatus projectorStatus) {
        realtimeService.notifyUpdate(resource, projectorStatus);
    }
}
