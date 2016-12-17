package com.pragmasphere.oika.automator.fluentlenium.po;

import com.pragmasphere.oika.automator.fluentlenium.widget.Menu;
import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.support.FindBy;

public class OikaFluentPage extends FluentPage {

    @FindBy(css = "#MenuACC1")
    public Menu menu;
}
