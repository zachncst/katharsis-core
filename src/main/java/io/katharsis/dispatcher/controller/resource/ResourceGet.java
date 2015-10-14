package io.katharsis.dispatcher.controller.resource;

import io.katharsis.dispatcher.controller.BaseController;
import io.katharsis.dispatcher.controller.HttpMethod;
import io.katharsis.queryParams.RequestParams;
import io.katharsis.repository.ResourceRepository;
import io.katharsis.request.dto.RequestBody;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.PathIds;
import io.katharsis.request.path.ResourcePath;
import io.katharsis.resource.exception.ResourceNotFoundException;
import io.katharsis.resource.include.IncludeFieldSetter;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.response.BaseResponse;
import io.katharsis.response.LinksInformation;
import io.katharsis.response.MetaInformation;
import io.katharsis.response.ResourceResponse;
import io.katharsis.utils.parser.TypeParser;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;

public class ResourceGet extends ResourceIncludeField {

    public ResourceGet(ResourceRegistry resourceRegistry, TypeParser typeParser, IncludeFieldSetter fieldSetter) {
        super(resourceRegistry, typeParser, fieldSetter);
    }

    /**
     * {@inheritDoc}
     *
     * Checks if requested resource method is acceptable - is a GET request for a resource.
     */
    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return !jsonPath.isCollection()
                && jsonPath instanceof ResourcePath
                && HttpMethod.GET.name().equals(requestType);
    }

    /**
     * {@inheritDoc}
     *
     * Passes the request to controller method.
     */
    @Override
    public BaseResponse<?> handle(JsonPath jsonPath, RequestParams requestParams, RequestBody requestBody)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String resourceName = jsonPath.getElementName();
        PathIds resourceIds = jsonPath.getIds();
        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
        if (registryEntry == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        String id = resourceIds.getIds().get(0);

        @SuppressWarnings("unchecked") Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry
                .getResourceInformation()
                .getIdField()
                .getType();
        Serializable castedId = typeParser.parse(id, idClass);
        ResourceRepository resourceRepository = registryEntry.getResourceRepository();
        @SuppressWarnings("unchecked")
        Object entity = resourceRepository.findOne(castedId, requestParams);
        MetaInformation metaInformation =
            getMetaInformation(resourceRepository, Collections.singletonList(entity), requestParams);
        LinksInformation linksInformation =
            getLinksInformation(resourceRepository, Collections.singletonList(entity), requestParams);
        includeFieldSetter.setIncludedElements(entity, requestParams);

        return new ResourceResponse(entity, jsonPath, requestParams, metaInformation, linksInformation);
    }
}
