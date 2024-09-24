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
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;

@ConnectorClass(displayNameKey = "cyberark.connector.display", configurationClass = CyberArkConfiguration.class)
public class CyberArkConnector implements Connector {

    private static final Log LOG = Log.getLog(CyberArkConnector.class);

    private CyberArkConfiguration configuration;
    private CyberArkConnection connection;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (CyberArkConfiguration)configuration;
        this.connection = new CyberArkConnection(this.configuration);
    }

    @Override
    public void dispose() {
        configuration = null;
        if (connection != null) {
            connection.dispose();
            connection = null;
        }
    }
}
