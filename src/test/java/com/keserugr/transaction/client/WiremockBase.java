package com.keserugr.transaction.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class WiremockBase {
    protected static WireMockServer wm;

    @BeforeAll
    static void beforeAll() {
        wm = new WireMockServer(0);
        wm.start();
    }

    @AfterAll
    static void afterAll() {
        if (wm != null) wm.stop();
    }
}
