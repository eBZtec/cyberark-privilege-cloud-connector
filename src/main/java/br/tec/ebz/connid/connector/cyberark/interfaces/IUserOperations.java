package br.tec.ebz.connid.connector.cyberark.interfaces;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.json.JSONObject;

import java.util.Set;

public interface IUserProcessing {

    Uid create(Set<Attribute> attributeSet);

    void changePassword(String uid, GuardedString password);

    void changeStatus(String uid, Boolean status);

    String update(Uid uid, Set<AttributeDelta> deltas);

    void delete(Uid uid);

    void search(Filter filter, ResultsHandler resultsHandler);

    void searchAll(ResultsHandler resultsHandler);

    void searchByFilter(Filter filter, ResultsHandler resultsHandler);

    ConnectorObject translateToConnectorObject(JSONObject user);

    ObjectClassInfo schema();
}
