package com.pragmasphere.oika.automator.fluentlenium.po;

import com.pragmasphere.oika.automator.commands.auth.Auth;
import com.pragmasphere.oika.automator.fluentlenium.configuration.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

@PageUrl("http://www.oikaoika.fr/mon-espace-reserve")
public class MonEspaceReserve extends FluentPage {
    @FindBy(css = "#espace-reserve input#Login")
    private FluentWebElement login;

    @FindBy(css = "#espace-reserve input#Pass")
    private FluentWebElement password;

    @FindBy(css = "#espace-reserve input[type='submit']")
    private FluentWebElement submit;

    public void login(final Auth auth) {
        login.write(auth.getLogin());
        password.write(auth.getPassword());
        submit.click();
    }
}
