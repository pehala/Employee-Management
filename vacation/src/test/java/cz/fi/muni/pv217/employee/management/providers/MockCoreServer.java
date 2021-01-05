package cz.fi.muni.pv217.employee.management.providers;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Collections;
import java.util.Map;

public class MockCoreServer implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockServer.stubFor(get(urlMatching("/api/employee/[0-9]+"))
            .willReturn(aResponse().withStatus(404)));
        wireMockServer.stubFor(get(urlPathMatching("/api/employee/(1|2)"))
            .willReturn(okJson("{" +
                                "\"username\": \"user\"" +
                                "}")));
        return Collections.singletonMap("core-service/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
