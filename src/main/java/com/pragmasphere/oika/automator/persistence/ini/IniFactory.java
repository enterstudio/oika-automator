package com.pragmasphere.oika.automator.persistence.ini;

import com.google.common.io.Files;
import org.ini4j.Ini;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class IniFactory implements FactoryBean<Ini> {
    @Autowired
    private String INIConfigurationFileName;

    @Override
    public Ini getObject() throws Exception {
        final File file = new File(INIConfigurationFileName);
        Files.touch(file);
        final Ini ini = new Ini(file);
        return ini;
    }

    @Override
    public Class<?> getObjectType() {
        return Ini.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
