package com.pragmasphere.oika.automator.custom;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class Prompt implements PromptProvider {
    public Prompt() {
    }

    @Override
    public String getPrompt() {
        return "oika>";
    }

    @Override
    public String getProviderName() {
        return "Oika prompt provider";
    }
}