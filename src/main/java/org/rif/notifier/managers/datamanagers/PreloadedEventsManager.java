package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.PreloadedEvents;
import org.rif.notifier.repositories.PreloadedEventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreloadedEventsManager {
    @Autowired
    private PreloadedEventsRepository preloadedEventsRepository;

    public List<PreloadedEvents> getAllPreloadedEvents(){
        return  preloadedEventsRepository.findAll();
    }

    public PreloadedEvents getPreloadedEventById(int id){
        return  preloadedEventsRepository.findById(id);
    }
}