package com.pragmasphere.oika.automator.custom;

import com.pragmasphere.oika.automator.Main;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

import java.io.IOError;
import java.io.IOException;

@Component
@Order(0)
public class Banner implements BannerProvider {
    public Banner() {
    }

    public String getBanner() {
        try {
            return IOUtils.toString(getClass().getResourceAsStream("banner.txt"));
        } catch (final IOException e) {
            throw new IOError(e);
        }
    }

    public String getVersion() {
        return Main.class.getPackage().getImplementationVersion();
    }

    public String getWelcomeMessage() {
        return "Oika Oika !";
    }

    public String getProviderName() {
        return "Oika Shell";
    }
}