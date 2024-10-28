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

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

public class CyberArkConnection {

    private static final Log LOG = Log.getLog(CyberArkConnection.class);

    private final CyberArkConfiguration configuration;
    private final CyberArkEndpoint cyberArkEndpoint;

    public CyberArkConnection(CyberArkConfiguration configuration) {
        this.configuration = configuration;
        this.cyberArkEndpoint = new CyberArkEndpoint(configuration);
    }

    public CyberArkEndpoint getCyberArkEndpoint() {
        return cyberArkEndpoint;
    }

    public CyberArkConfiguration getConfiguration() {
        return configuration;
    }

    public void dispose() {
        try {
            cyberArkEndpoint.logoff();
            LOG.ok("User logged off successfully");
        } catch (Exception e) {
            throw new ConfigurationException("Could not logoff user, reason: " + e.getMessage());
        }
    }
}