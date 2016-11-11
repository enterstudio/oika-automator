package com.pragmasphere.oika.automator.fluentlenium.configuration;

import org.fluentlenium.adapter.FluentAdapter;
import org.fluentlenium.configuration.FluentConfiguration;
import org.fluentlenium.core.hook.wait.Wait;

@FluentConfiguration(configurationDefaults = Configuration.class)
@Wait
public abstract class FluentScript extends FluentAdapter implements Runnable {

    @Override
    public void run() {
        try {
            initFluent(newWebDriver());
            runScript();
        } finally {
            if (getDriver() != null) {
                getDriver().quit();
            }
            releaseFluent();
        }
    }

    abstract protected void runScript();
}
