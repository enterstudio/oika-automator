package com.pragmasphere.oika.automator.fluentlenium.configuration;

import org.fluentlenium.adapter.FluentStandaloneRunnable;
import org.fluentlenium.configuration.FluentConfiguration;
import org.fluentlenium.core.hook.wait.Wait;

@FluentConfiguration(configurationDefaults = Configuration.class)
@Wait
public abstract class FluentScript extends FluentStandaloneRunnable {

}
