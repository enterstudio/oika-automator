package com.pragmasphere.oika.automator.commands.auth;

import com.pragmasphere.oika.automator.persistence.PersistenceBackend;
import com.pragmasphere.oika.automator.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.table.Table;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.pragmasphere.oika.automator.custom.TableUtils.buildTable;

@Component
public class AuthCommand implements CommandMarker {

    @Autowired
    private PersistenceBackend backend;

    @Autowired
    private SecurityService security;

    @CliCommand(value = "config auth", help = "Enregistre l'identifiant et le mot de passe du compte à utiliser")
    public Table auth(
            @CliOption(key = "login", mandatory = true, help = "Identifiant du compte")
            final String login,
            @CliOption(key = "password", mandatory = true, help = "Mot de passe du compte")
            final String password) {

        final Auth auth = new Auth(login, security.encrypt(password));

        backend.delete(auth);
        backend.persist(auth);
        backend.flush();

        return buildTable(Arrays.asList(auth), "login", "password");
    }

}