package cz.fi.muni.pv217.employee.management.providers;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class MockLeaveServer implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(wireMockConfig()
                .dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(get(urlPathEqualTo("/api/leave/employee/1"))
                .withQueryParam("from", equalTo("2018-01-01"))
                .withQueryParam("to", equalTo("2018-01-31"))
                .willReturn(okJson("[]")));

        wireMockServer.stubFor(get(urlPathEqualTo("/api/leave/employee/2"))
                .withQueryParam("from", equalTo("2018-01-01"))
                .withQueryParam("to", equalTo("2018-01-31"))
                .willReturn(okJson(
                                "[\n" +
                                        "  {\n" +
                                        "    \"id\":1,\n" +
                                        "    \"date\":\"2018-01-03\",\n" +
                                        "    \"hours\": 7,\n" +
                                        "    \"paid\": true\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"id\":2,\n" +
                                        "    \"date\":\"2018-01-25\",\n" +
                                        "    \"hours\":5,\n" +
                                        "    \"paid\": false\n" +
                                        "  }\n" +
                                        "]"
                        )));

        return Collections.singletonMap("leave-api/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
