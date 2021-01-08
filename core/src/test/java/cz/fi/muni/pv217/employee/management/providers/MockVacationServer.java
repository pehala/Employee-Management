package cz.fi.muni.pv217.employee.management.providers;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class MockVacationServer implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(wireMockConfig()
                .dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(get(urlPathEqualTo("/api/vacation/employee/1/date"))
                .withQueryParam("date", equalTo("2020-12-28"))
                .willReturn(okJson("false")));

        wireMockServer.stubFor(get(urlPathEqualTo("/api/vacation/employee/1/date"))
                .withQueryParam("date", equalTo("2020-12-29"))
                .willReturn(okJson("true")));

        return Collections.singletonMap("vacation-service/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
