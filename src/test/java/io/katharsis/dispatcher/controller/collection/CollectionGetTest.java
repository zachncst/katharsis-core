package io.katharsis.dispatcher.controller.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.dispatcher.controller.BaseControllerTest;
import io.katharsis.request.path.JsonPath;
import io.katharsis.queryParams.RequestParams;
import io.katharsis.response.BaseResponse;
import org.junit.Assert;
import org.junit.Test;

public class CollectionGetTest extends BaseControllerTest {

    private static final String REQUEST_TYPE = "GET";

    @Test
    public void onGivenRequestCollectionGetShouldAcceptIt() {
        // GIVEN
        JsonPath jsonPath = pathBuilder.buildPath("/tasks/");
        CollectionGet sut = new CollectionGet(resourceRegistry, typeParser);

        // WHEN
        boolean result = sut.isAcceptable(jsonPath, REQUEST_TYPE);

        // THEN
        Assert.assertEquals(result, true);
    }

    @Test
    public void onGivenRequestCollectionGetShouldDenyIt() {
        // GIVEN
        JsonPath jsonPath = pathBuilder.buildPath("/tasks/2");
        CollectionGet sut = new CollectionGet(resourceRegistry, typeParser);

        // WHEN
        boolean result = sut.isAcceptable(jsonPath, REQUEST_TYPE);

        // THEN
        Assert.assertEquals(result, false);
    }

    @Test
    public void onGivenRequestCollectionGetShouldHandleIt() {
        // GIVEN

        JsonPath jsonPath = pathBuilder.buildPath("/tasks/");
        CollectionGet sut = new CollectionGet(resourceRegistry, typeParser);

        // WHEN
        BaseResponse<?> response = sut.handle(jsonPath, new RequestParams(new ObjectMapper()), null, null);

        // THEN
        Assert.assertNotNull(response);
    }

    @Test
    public void onGivenRequestCollectionWithIdsGetShouldHandleIt() {
        // GIVEN

        JsonPath jsonPath = pathBuilder.buildPath("/tasks/1,2");
        CollectionGet sut = new CollectionGet(resourceRegistry, typeParser);

        // WHEN
        BaseResponse<?> response = sut.handle(jsonPath, new RequestParams(new ObjectMapper()), null, null);

        // THEN
        Assert.assertNotNull(response);
    }
}
