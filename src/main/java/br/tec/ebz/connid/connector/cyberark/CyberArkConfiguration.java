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

    private String domain = "";
    private String user = "";
    private GuardedString password;
    private String method = "CyberArk";

    @Override
    public void validate() {
        String exceptionMsg = "";

        if (domain.isEmpty() || domain.isBlank()) {
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
            required = true
    )
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @ConfigurationProperty(
            displayMessageKey = "cyberark.config.user",
            helpMessageKey = "cyberark.config.user.help",
            required = true
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
            confidential = true
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
            required = true
    )
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}