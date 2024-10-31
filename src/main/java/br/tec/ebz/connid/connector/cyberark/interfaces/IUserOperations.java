package br.tec.ebz.connid.connector.cyberark.interfaces;

import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.net.URISyntaxException;
import java.util.Set;

public interface IUserOperations {

    String type();

    Uid create(Set<Attribute> attributeSet);

    void changePassword(String uid, Set<AttributeDelta> modifications);

    void enableUser(String uid);

    void disableUser(String uid);

    void activateUser(String uid);

    String update(Uid uid, Set<AttributeDelta> deltas);

    JSONObject getUserDetails(String id);

    void delete(Uid uid);

    void search(Filter filter, ResultsHandler resultsHandler, OperationOptions options);

    void searchAll(ResultsHandler resultsHandler, OperationOptions options);

    void searchByFilter(Filter filter, ResultsHandler resultsHandler, OperationOptions options);

    ConnectorObject translateToConnectorObject(JSONObject user);

    ObjectClassInfo schema();
}
