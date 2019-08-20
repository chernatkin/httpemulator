package ru.hh.httpemulator.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.httpemulator.server.engine.CriteriaHttpEngine;
import ru.hh.httpemulator.server.engine.ScenarioEngine;
import ru.hh.httpemulator.server.scenario.OAuthCodeCallbackScenario;
import ru.hh.httpemulator.server.scenario.TimeoutScenario;
import ru.hh.nab.starter.NabProdConfig;

@Configuration
@Import({
    NabProdConfig.class,
    HttpResourcesConfig.class,

    CriteriaHttpEngine.class,
    ScenarioEngine.class,

    OAuthCodeCallbackScenario.class,
    TimeoutScenario.class,
})
public class EmulatorProdConfig {

}
