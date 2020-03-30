package org.kie.kogito.mgmt;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

@ApplicationScoped
public class VertxRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxRouter.class);

    @Inject
    @ConfigProperty(name = "kogito.dataindex.http.url", defaultValue = "http://localhost:8180")
    String dataIndexHttpURL;

    @Inject
    Vertx vertx;

    private String resource;

    @PostConstruct
    public void init() {
        resource = vertx.fileSystem()
                .readFileBlocking("META-INF/resources/index.html")
                .toString(UTF_8)
                .replace("__DATA_INDEX_ENDPOINT__", "\"" + dataIndexHttpURL + "/graphql\"");
    }

    void setupRouter(@Observes Router router) {
        router.route("/").handler(ctx -> ctx.response().putHeader("location", "/ProcessInstances/").setStatusCode(302).end());
        router.route("/ProcessInstances*").handler(ctx -> handle(ctx));
        router.route("/DomainExplorer*").handler(ctx -> handle(ctx));
        router.route().handler(StaticHandler.create());
    }

    public void handle(RoutingContext context) {
        try {
            context.response()
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf8")
                    .end(resource);
        } catch (Exception ex) {
            LOGGER.error("Error handling index.html", ex);
            context.fail(500, ex);
        }
    }


}
