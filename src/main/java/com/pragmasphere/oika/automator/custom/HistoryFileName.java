package com.pragmasphere.oika.automator.custom;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class HistoryFileName implements HistoryFileNameProvider {
    @Override
    public String getHistoryFileName() {
        return "oika.log";
    }

    @Override
    public String getProviderName() {
        return "Oika history provider";
    }
}
