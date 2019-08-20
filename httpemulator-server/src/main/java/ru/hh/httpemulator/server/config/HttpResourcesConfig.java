package ru.hh.httpemulator.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.httpemulator.server.resources.CriteriaController;
import ru.hh.httpemulator.server.resources.RequestController;

@Configuration
@Import({
    CriteriaController.class,
    RequestController.class,
})
public class HttpResourcesConfig {
}
