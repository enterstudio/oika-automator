package com.pragmasphere.oika.automator.fluentlenium.po;

import com.pragmasphere.oika.automator.fluentlenium.configuration.FluentPage;
import com.pragmasphere.oika.automator.fluentlenium.widget.Menu;
import org.openqa.selenium.support.FindBy;

public class OikaFluentPage extends FluentPage {

    @FindBy(css = "#MenuACC1")
    public Menu menu;
}
