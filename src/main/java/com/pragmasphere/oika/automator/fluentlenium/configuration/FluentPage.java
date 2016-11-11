package com.pragmasphere.oika.automator.fluentlenium.configuration;

import org.fluentlenium.core.FluentControl;

import java.net.URI;

public class FluentPage extends org.fluentlenium.core.FluentPage {
    public FluentPage() {
    }

    public FluentPage(final FluentControl control) {
        super(control);
    }

    @Override
    public void isAt() {
        super.isAt();

        final String url = getUrl();
        if (url != null) {
            final String absoluteUrl = buildUrl(url);
            final String currentUrl = getDriver().getCurrentUrl();
            if (!currentUrl.equalsIgnoreCase(absoluteUrl)) {
                throw new AssertionError(
                        String.format("WebDriver URL (%s) is not the same as URL of the page (%s) (%s)", currentUrl, absoluteUrl,
                                getClass().getName()));
            }
        }
    }

    private String buildUrl(String url) {
        String baseUrl = getBaseUrl();
        if (baseUrl != null) {
            String configBaseUrl = baseUrl;
            if (configBaseUrl != null) {
                if (configBaseUrl.endsWith("/")) {
                    configBaseUrl = configBaseUrl.substring(0, configBaseUrl.length() - 1);
                }
                baseUrl = configBaseUrl;
            }
        }
        if (baseUrl != null) {
            final URI uri = URI.create(url);
            if (!uri.isAbsolute()) {
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                url = baseUrl + url;
            }
        }
        if (url == null) {
            url = baseUrl;
        }
        return url;
    }

}
