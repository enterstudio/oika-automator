package com.pragmasphere.oika.automator;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.shell.Bootstrap;

import java.io.IOException;

public class Main {

    public static void main(final String[] args) throws IOException {
        Bootstrap.main(ArrayUtils.add(args, "--disableInternalCommands"));
    }
}
