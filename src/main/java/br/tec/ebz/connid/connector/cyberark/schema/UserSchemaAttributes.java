package br.tec.ebz.connid.connector.cyberark.schema;

import org.identityconnectors.common.StringUtil;

import java.util.List;

public enum UserSchemaAttributes {
    ID("id", false, Integer.class, 1),
    USERNAME("username", true, String.class, 1),
    SOURCE("source", false, String.class, 1),
    USER_TYPE("userType", false, String.class, 1),
    GROUPS_MEMBERSHIP("groupsMembership", false, List.class, -1),
    VAULT_AUTHORIZATION("vaultAuthorization", false, List.class, -1),
    LOCATION("location", false, String.class, 1),
    COMPONENT_USER("componentUser", false, Boolean.class, 1),
    ENABLE_USER("enableUser", false, Boolean.class, 1),
    CHANGE_PASS_ON_NEXT_LOGON("changePassOnNextLogon", false, Boolean.class, 1),
    EXPIRY_DATE("expiryDate", false, Long.class, 1),
    SUSPENDED("suspended", false, Boolean.class, 1),
    LAST_SUCCESSFUL_LOGIN_DATE("lastSuccessfullLoginDate", false, Long.class, 1),
    UNAUTHORIZED_INTERFACES("unAuthorizedInterfaces", false, List.class, -1),
    AUTHENTICATION_METHOD("authenticationMethod", false, List.class, -1),
    PASSWORD_NEVER_EXPIRES("passwordNeverExpires", false, Boolean.class, 1),
    DISTINGUISHED_NAME("distinguishedName", false, String.class, 1),
    DESCRIPTION("description", false, String.class, 1),
    INITIAL_PASSWORD("initialPassword", false, String.class, 1),

    // Business Address
    BUSINESS_ADDRESS("businessAddress", false, Object.class, 1),
    WORK_STREET("workStreet",false, String.class, 1),
    WORK_CITY("workCity",false, String.class, 1),
    WORK_STATE("workState",false, String.class, 1),
    WORK_ZIP("workZip",false, String.class, 1),
    WORK_COUNTRY("workCountry", false, String.class, 1),

    // Internet
    INTERNET("internet", false, Object.class, 1),
    HOME_PAGE("homePage", false, String.class, 1),
    HOME_EMAIL("homeEmail", false, String.class, 1),
    BUSINESS_EMAIL("businessEmail", false, String.class, 1),
    OTHER_EMAIL("otherEmail", false, String.class, 1),

    // Phones
    PHONES("phones", false, Object.class, 1),
    HOME_NUMBER("homeNumber", false, String.class, 1),
    BUSINESS_NUMBER("businessNumber", false, String.class, 1),
    CELLULAR_NUMBER("cellularNumber", false, String.class, 1),
    FAX_NUMBER("faxNumber", false, String.class, 1),
    PAGER_NUMBER("pagerNumber", false, String.class, 1),

    // Personal Details
    PERSONAL_DETAILS("personalDetails", false, Object.class, 1),
    STREET("street", false, String.class, 1),
    CITY("city", false, String.class, 1),
    STATE("state", false, String.class, 1),
    ZIP("zip", false, String.class, 1),
    COUNTRY("country", false, String.class, 1),
    TITLE("title", false, String.class, 1),
    ORGANIZATION("organization", false, String.class, 1),
    DEPARTMENT("department", false, String.class, 1),
    PROFESSION("profession", false, String.class, 1),
    FIRST_NAME("firstName", false, String.class, 1),
    MIDDLE_NAME("middleName", false, String.class, 1),
    LAST_NAME("lastName", false, String.class, 1);

    private final String attribute;
    private final boolean required;
    private final Class<?> type;
    private final Integer ocorrence;

    UserSchemaAttributes(String attribute, boolean required, Class<?> type, Integer ocorrence) {
        this.attribute = attribute;
        this.required = required;
        this.type = type;
        this.ocorrence = ocorrence;
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean isRequired() {
        return required;
    }

    public Class<?> getType() {
        return type;
    }

    public Integer getOcorrence() {
        return ocorrence;
    }

    public static UserSchemaAttributes findAttribute(String name){
        if (StringUtil.isBlank(name)){
            return null;
        }

        for (UserSchemaAttributes attr : values()){
            if (name.equals(attr.getAttribute())){
                return attr;
            }
        }

        return null;
    }
}

