package com.pragmasphere.oika.automator.fluentlenium.widget;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebElement;

import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class Menu extends FluentWebElement {

    public Menu(final WebElement element, final FluentControl control, final ComponentInstantiator instantiator) {
        super(element, control, instantiator);
    }

    public FluentWebElement reunions() {
        return $("a", withText().startsWith("Réunions")).first();
    }
}
