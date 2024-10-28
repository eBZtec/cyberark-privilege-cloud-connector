package br.tec.ebz.connid.connector.cyberark;

import br.tec.ebz.connid.connector.cyberark.operations.UserOperations;
import br.tec.ebz.connid.connector.cyberark.schema.UserSchemaAttributes;
import br.tec.ebz.connid.connector.cyberark.utils.BasicTestConnection;
import br.tec.ebz.connid.connector.cyberark.utils.GetUser;
import br.tec.ebz.connid.connector.cyberark.utils.ListResultHandler;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class SearchOpTest {

    private ConnectorFacade connectorFacade;
    private GetUser userTest;
    private OperationOptions operationOptions;

    @BeforeTest
    public void init() {
        BasicTestConnection basicTestConnection = new BasicTestConnection();
        connectorFacade = basicTestConnection.getTestConnection();

        Map<String, Object> options = new HashMap<>();
        options.put(OperationOptions.OP_PAGE_SIZE, 5);
        options.put(OperationOptions.OP_PAGED_RESULTS_OFFSET, 0);
        operationOptions = new OperationOptions(options);

        userTest = new GetUser();
    }

    @Test
    public void searchUserByUid() {
        String uid = "44";

        ListResultHandler resultHandler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(Uid.NAME, uid);
        EqualsFilter filter = new EqualsFilter(attribute);

        connectorFacade.search(UserOperations.OBJECT_CLASS, filter, resultHandler, operationOptions);

        AssertJUnit.assertNotNull(resultHandler);
        AssertJUnit.assertEquals(1, resultHandler.getObjects().size());
    }

    @Test
    public void searchUserByName() {
        String name = "createTestUser289";

        ListResultHandler resultHandler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(Name.NAME, name);
        EqualsFilter filter = new EqualsFilter(attribute);

        connectorFacade.search(UserOperations.OBJECT_CLASS, filter, resultHandler, operationOptions);

        AssertJUnit.assertNotNull(resultHandler);
        AssertJUnit.assertEquals(1, resultHandler.getObjects().size());
    }

    @Test
    public void searchUserByUserName() {
        String name = "createTestUser802";

        ListResultHandler resultHandler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(UserSchemaAttributes.USERNAME.getAttribute(), name);
        EqualsFilter filter = new EqualsFilter(attribute);

        connectorFacade.search(UserOperations.OBJECT_CLASS, filter, resultHandler, operationOptions);

        AssertJUnit.assertNotNull(resultHandler);
        AssertJUnit.assertEquals(1, resultHandler.getObjects().size());
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void searchRaiseUnsupportedException() {
        String name = "Analyst";

        ListResultHandler resultHandler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(UserSchemaAttributes.TITLE.getAttribute(), name);
        EqualsFilter filter = new EqualsFilter(attribute);

        connectorFacade.search(UserOperations.OBJECT_CLASS, filter, resultHandler, operationOptions);

        AssertJUnit.assertNotNull(resultHandler);
        AssertJUnit.assertEquals(1, resultHandler.getObjects().size());
    }

    @Test
    public void searchUserDoesNotExists() {
        String name = "createTestUser999999";

        ListResultHandler resultHandler = new ListResultHandler();
        Attribute attribute = AttributeBuilder.build(Name.NAME, name);
        EqualsFilter filter = new EqualsFilter(attribute);

        connectorFacade.search(UserOperations.OBJECT_CLASS, filter, resultHandler, operationOptions);

        AssertJUnit.assertNotNull(resultHandler);
        AssertJUnit.assertEquals(0, resultHandler.getObjects().size());
    }

    @Test
    public void searchAllUsers() {
        ListResultHandler resultHandler = new ListResultHandler();

        connectorFacade.search(UserOperations.OBJECT_CLASS, null, resultHandler, operationOptions);

        AssertJUnit.assertNotNull(resultHandler);
        AssertJUnit.assertEquals(22, resultHandler.getObjects().size());
    }
}
