package br.tec.ebz.connid.connector.cyberark.interfaces;

import kong.unirest.core.json.JSONObject;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.net.URISyntaxException;
import java.util.Set;

public interface IUserOperations {

    String type();

    Uid create(Set<Attribute> attributeSet) throws URISyntaxException;

    void changePassword(String uid, GuardedString password);

    void enableUser(String uid) throws URISyntaxException;

    void disableUser(String uid) throws URISyntaxException;

    void activateUser(String uid) throws URISyntaxException;

    String update(Uid uid, Set<AttributeDelta> deltas);

    void delete(Uid uid) throws URISyntaxException;

    void search(Filter filter, ResultsHandler resultsHandler) throws URISyntaxException;

    void searchAll(ResultsHandler resultsHandler) throws URISyntaxException;

    void searchByFilter(Filter filter, ResultsHandler resultsHandler) throws URISyntaxException;

    ConnectorObject translateToConnectorObject(JSONObject user);

    ObjectClassInfo schema();
}
