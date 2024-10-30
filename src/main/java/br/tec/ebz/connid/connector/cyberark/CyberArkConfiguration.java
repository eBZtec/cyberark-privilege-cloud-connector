/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.tec.ebz.connid.connector.cyberark;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class CyberArkConfiguration extends AbstractConfiguration {

    private static final Log LOG = Log.getLog(CyberArkConfiguration.class);

    private String server = "";
    private String user = "";
    private GuardedString password;
    private String method = "CyberArk";
    private Boolean verifySsl = true;

    @Override
    public void validate() {
        String exceptionMsg = "";

        if (server.isEmpty() || server.isBlank()) {
            exceptionMsg = "Domain is required";
        } else if (user.isEmpty() || user.isBlank()) {
            exceptionMsg = "User is required";
        } else if (password == null) {
            exceptionMsg = "Password is required";
        }

        if (!exceptionMsg.isBlank()) {
            throw new ConfigurationException("Configuration failed, reason: " + exceptionMsg);
        }
    }

    @ConfigurationProperty(
            displayMessageKey = "cyberark.config.domain",
            helpMessageKey = "cyberark.config.domain.help",
            required = true,
            order = 1
    )
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @ConfigurationProperty(
            displayMessageKey = "cyberark.config.user",
            helpMessageKey = "cyberark.config.user.help",
            required = true,
            order = 2
    )
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @ConfigurationProperty(
            displayMessageKey = "cyberark.config.password",
            helpMessageKey = "cyberark.config.password.help",
            required = true,
            confidential = true,
            order = 3
    )
    public GuardedString getPassword() {
        return password;
    }

    public void setPassword(GuardedString password) {
        this.password = password;
    }

    @ConfigurationProperty(
            displayMessageKey = "cyberark.config.method",
            helpMessageKey = "cyberark.config.method.help",
            order = 4
    )
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @ConfigurationProperty(
            displayMessageKey = "cyberark.config.verifySsl",
            helpMessageKey = "cyberark.config.verifySsl.help",
            order = 5
    )
    public Boolean getVerifySsl() {
        return verifySsl;
    }

    public void setVerifySsl(Boolean verifySsl) {
        this.verifySsl = verifySsl;
    }
}