package cz.fi.muni.pv217.employee.management.providers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import cz.fi.muni.pv217.employee.management.util.TestUtil;
import org.jboss.logging.Logger;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * https://github.com/quarkusio/quarkus/blob/master/integration-tests/oidc-wiremock/src/test/java/io/quarkus/it/keycloak/KeycloakTestResource.java
 */
public class MockKeycloakTestResource implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOG = Logger.getLogger(MockKeycloakTestResource.class);
    private WireMockServer server;

    @Override
    public Map<String, String> start() {

        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        server.stubFor(get(urlEqualTo("/auth/realms/quarkus/.well-known/openid-configuration"))
            .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody("{\n" +
                          "    \"jwks_uri\": \"" + server.baseUrl() +
                          "/auth/realms/quarkus/protocol/openid-connect/certs\"\n" +
                          "}")));
        server.stubFor(get(urlEqualTo("/auth/realms/quarkus/protocol/openid-connect/certs"))
            .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody("{\n" +
                          "    \"keys\": [\n " +
                          "        {\n "+
                          "         \"kty\" : \"oct\",\n"+
                          "         \"kid\" : \"1\",\n"+
                          "         \"alg\" : \"HS256\",\n"+
                          "         \"k\"   : \""+ TestUtil.HS256_SECRET + "\"\n"+
                          "       }"+
                          "   ]"+
                          "}")));
        LOG.infof("Keycloak started in mock mode: %s", server.baseUrl());
        return Collections.singletonMap("quarkus.oidc.auth-server-url", server.baseUrl() + "/auth/realms/quarkus");
    }

    @Override
    public synchronized void stop() {
        if (server != null) {
            server.stop();
            LOG.info("Keycloak was shut down");
            server = null;
        }
    }
}
