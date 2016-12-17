package com.pragmasphere.oika.automator.fluentlenium.configuration;

import org.fluentlenium.configuration.ConfigurationDefaults;

public class Configuration extends ConfigurationDefaults {
    @Override
    public String getBaseUrl() {
        return "www.moncomptevdi.fr/oikaoika";
    }

    @Override
    public String getRemoteUrl() {
        return super.getRemoteUrl();
    }

    @Override
    public String getWebDriver() {
        return "phantomjs";
    }
}
