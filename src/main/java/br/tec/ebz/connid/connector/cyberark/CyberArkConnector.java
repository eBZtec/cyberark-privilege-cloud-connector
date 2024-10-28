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

import br.tec.ebz.connid.connector.cyberark.operations.UserOperations;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;

import java.util.List;
import java.util.Set;

@ConnectorClass(displayNameKey = "cyberark.connector.display", configurationClass = CyberArkConfiguration.class)
public class CyberArkConnector implements Connector, TestOp, CreateOp, SearchOp<Filter>, DeleteOp, UpdateDeltaOp {

    private static final Log LOG = Log.getLog(CyberArkConnector.class);

    private CyberArkConfiguration configuration;
    private CyberArkConnection connection;
    private UserOperations userOperations;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        try {
            LOG.info("Initializing CyberArk connection.");
            this.configuration = (CyberArkConfiguration) configuration;
            this.connection = new CyberArkConnection(this.configuration);

            this.userOperations = new UserOperations(this.connection.getCyberArkEndpoint());

            LOG.info("CyberArk connection initialized successfully.");
        } catch (Exception e) {
            throw new ConnectionFailedException("Could not connect to CyberArk API, reason: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        configuration = null;
        if (connection != null) {
            connection.dispose();
            connection = null;
        }
    }

    @Override
    public void test() {

    }

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> createAttributes, OperationOptions options) {
        Uid uid;

        try {
            if (objectClass.is(UserOperations.OBJECT_CLASS_NAME)) {
                LOG.info("Starting process to create user");
                uid = userOperations.create(createAttributes);
            } else {
                throw new UnsupportedOperationException("Object class " + objectClass.getObjectClassValue()+ " not supported");
            }
        } catch (Exception e) {
            throw new ConnectorException("Could not create object class " + objectClass.getObjectClassValue() + ", reason: " + e.getMessage());
        }

        return uid;
    }

    @Override
    public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass, OperationOptions options) {
        return new FilterTranslator<Filter>() {
            @Override
            public List<Filter> translate(Filter filter) {
                return CollectionUtil.newList(filter);
            }
        };
    }

    @Override
    public void executeQuery(ObjectClass objectClass, Filter query, ResultsHandler handler, OperationOptions options) {
        try {
            if (objectClass.is(UserOperations.OBJECT_CLASS_NAME)) {
                LOG.info("Starting process to create user");
                userOperations.search(query, handler, options);
            } else {
                throw new UnsupportedOperationException("Object class " + objectClass.getObjectClassValue()+ " not supported");
            }
        } catch (Exception e) {
            throw new ConnectorException("Could not search object class " + objectClass.getObjectClassValue() + ", reason: " + e.getMessage());
        }
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions options) {
        try {
            if (objectClass.is(UserOperations.OBJECT_CLASS_NAME)) {
                LOG.info("Starting process to delete user");
                userOperations.delete(uid);
            } else {
                throw new UnsupportedOperationException("Object class " + objectClass.getObjectClassValue()+ " not supported");
            }
        } catch (Exception e) {
            throw new ConnectorException("Could not create object class " + objectClass.getObjectClassValue() + ", reason: " + e.getMessage());
        }
    }

    @Override
    public Set<AttributeDelta> updateDelta(ObjectClass objectClass, Uid uid, Set<AttributeDelta> modifications, OperationOptions options) {
        String newUid;

        if (objectClass.is(UserOperations.OBJECT_CLASS_NAME)) {
            newUid = userOperations.update(uid, modifications);
        } else {
            throw new UnsupportedOperationException("Object class " + objectClass.getObjectClassValue()+ " not supported");
        }
        return Set.of();
    }
}
