package com.sequenceiq.redbeams.service.cloud;

import com.sequenceiq.cloudbreak.cloud.notification.model.ResourceNotification;
import com.sequenceiq.cloudbreak.cloud.service.Persister;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class CloudResourcePersisterService implements Persister<ResourceNotification> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudResourcePersisterService.class);

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Override
    public ResourceNotification persist(ResourceNotification notification) {
        LOGGER.debug("Resource allocation notification received: {}", notification);
        //Long stackId = notification.getCloudContext().getId();
        //CloudResource cloudResource = notification.getCloudResource();
        //Resource resource = conversionService.convert(cloudResource, Resource.class);
        //Optional<Resource> persistedResource = resourceService.findByStackIdAndNameAndType(stackId, cloudResource.getName(), cloudResource.getType());
        //if (persistedResource.isPresent()) {
        //    LOGGER.debug("Trying to persist a resource (name: {}, type: {}, stackId: {}) that is already persisted, skipping..",
        //            cloudResource.getName(), cloudResource.getType().name(), stackId);
        //    return notification;
        //}
        //resource.setStack(findStackById(stackId));
        //resourceService.save(resource);
        return notification;
    }

    @Override
    public ResourceNotification update(ResourceNotification notification) {
        LOGGER.debug("Resource update notification received: {}", notification);
        //Long stackId = notification.getCloudContext().getId();
        //CloudResource cloudResource = notification.getCloudResource();
        //Resource persistedResource = resourceService.findByStackIdAndNameAndType(stackId, cloudResource.getName(), cloudResource.getType())
        //        .orElseThrow(notFound("resource", cloudResource.getName()));
        //Resource resource = conversionService.convert(cloudResource, Resource.class);
        //updateWithPersistedFields(resource, persistedResource);
        //resource.setStack(findStackById(stackId));
        //resourceService.save(resource);
        return notification;
    }

    @Override
    public ResourceNotification delete(ResourceNotification notification) {
        LOGGER.debug("Resource deletion notification received: {}", notification);
        //Long stackId = notification.getCloudContext().getId();
        //CloudResource cloudResource = notification.getCloudResource();
        //resourceService.findByStackIdAndNameAndType(stackId, cloudResource.getName(), cloudResource.getType())
        //        .ifPresent(value -> resourceService.delete(value));
        return notification;
    }

    @Override
    public ResourceNotification retrieve(ResourceNotification data) {
        return null;
    }

    //private void updateWithPersistedFields(Resource resource, Resource persistedResource) {
    //    if (persistedResource != null) {
    //        resource.setId(persistedResource.getId());
    //        resource.setInstanceGroup(persistedResource.getInstanceGroup());
    //    }
    //}

}
