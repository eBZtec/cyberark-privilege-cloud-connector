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

    void changePassword(String uid, GuardedString password);

    void enableUser(String uid);

    void disableUser(String uid);

    void activateUser(String uid);

    String update(Uid uid, Set<AttributeDelta> deltas);

    void delete(Uid uid);

    void search(Filter filter, ResultsHandler resultsHandler);

    void searchAll(ResultsHandler resultsHandler);

    void searchByFilter(Filter filter, ResultsHandler resultsHandler);

    ConnectorObject translateToConnectorObject(JSONObject user);

    ObjectClassInfo schema();
}
