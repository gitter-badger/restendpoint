/*
 * Copyright (C) 2014 Andrei Varabyeu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github.avarabyeu.restendpoint.http.mock;

import com.github.avarabyeu.restendpoint.http.*;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.github.avarabyeu.restendpoint.serializer.ByteArraySerializer;
import com.github.avarabyeu.restendpoint.serializer.StringSerializer;
import com.github.avarabyeu.wills.Will;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.commons.codec.binary.Base64;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * {@link com.github.avarabyeu.restendpoint.http.RestEndpoints} tests
 *
 * @author avarabyeu
 */
public class RestEndpointsTest extends BaseRestEndointTest {

    public static final String HTTP_TEST_URK = "http://localhost:" + GuiceTestModule.MOCK_PORT;
    public static final String ECHO_STRING = "Hello world!";
    public static final String RESOURCE = "/";


    private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);

    @BeforeClass
    public static void before() throws IOException {
        server.play(GuiceTestModule.MOCK_PORT);
    }

    @AfterClass
    public static void after() throws IOException {
        server.shutdown();
    }


    @Test
    public void testDefault() throws RestEndpointIOException {
        RestEndpoint endpoint = RestEndpoints.createDefault(HTTP_TEST_URK);
        Assert.assertThat(endpoint, notNullValue());

        server.enqueue(prepareResponse(ECHO_STRING).setHeader(CONTENT_TYPE_HEADER, MediaType.PLAIN_TEXT_UTF_8));
        Will<String> helloRS = endpoint.post(RESOURCE, ECHO_STRING, String.class);
        Assert.assertThat(helloRS.obtain(), is(ECHO_STRING));

    }

    /**
     * Put wrong serializer into non-default configuration
     *
     * @throws RestEndpointIOException
     */
    @Test(expected = SerializerException.class)
    public void testNoSerializer() throws RestEndpointIOException {
        RestEndpoint endpoint = RestEndpoints.create().withBaseUrl(HTTP_TEST_URK)
                .withSerializer(new ByteArraySerializer())
                .build();
        Assert.assertThat(endpoint, notNullValue());

        server.enqueue(prepareResponse(ECHO_STRING));
        Will<String> helloRS = endpoint.post(RESOURCE, ECHO_STRING, String.class);
        Assert.assertThat(helloRS.obtain(), is(ECHO_STRING));
    }

    @Test
    public void testBuilderHappy() throws RestEndpointIOException {
        RestEndpoint endpoint = RestEndpoints.create().withBaseUrl(HTTP_TEST_URK)
                .withSerializer(new StringSerializer())
                .build();
        Assert.assertThat(endpoint, notNullValue());

        server.enqueue(prepareResponse(ECHO_STRING));
        Will<String> helloRS = endpoint.post(RESOURCE, ECHO_STRING, String.class);
        Assert.assertThat(helloRS.obtain(), is(ECHO_STRING));
    }

    @Test
    public void testBuilderBasicAuth() throws RestEndpointIOException, InterruptedException {
        RestEndpoint endpoint = RestEndpoints.create().withBaseUrl(HTTP_TEST_URK)
                .withSerializer(new StringSerializer()).withBasicAuth("login", "password")
                .build();
        Assert.assertThat(endpoint, notNullValue());

        server.enqueue(prepareResponse(ECHO_STRING));
        endpoint.post(RESOURCE, ECHO_STRING, String.class).obtain();

        String basicAuthHeader = server.takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertThat(basicAuthHeader, is("Basic " + Base64.encodeBase64String("login:password".getBytes())));
    }

    //TODO add test for SSL
}
