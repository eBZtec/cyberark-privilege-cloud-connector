# CyberArk Privilege Cloud Connector

Connector to provisioning Users for CyberArk Privilege Cloud.

## Capabilities and Features

| Features          |         |                                                                  |
|-------------------|---------|------------------------------------------------------------------|
| Schema            | YES     | Users only                                                       |
| Live Sync         | NO      |                                                                  |
| Password          | YES     | Initial and change password                                      |
| Activation        | YES     |                                                                  |
| Filtering         | PARTIAL | Limited to the CyberArk API REST                                 |
| Native Attributes | YES     | Use ri:username instead icfs:name. Use ri:id instead of icfs:uid |
| Provisioning      | YES     | Users only                                                       |

## Interoperability

In theory, it should work with any CyberArk Privilege Cloud version. The connector uses the *Second Generation* of Privilege Cloud SDK.

The connector authenticates with 3 types of Method:
- CyberArk (default)
- LDAP
- RADIUS

Those methods authenticate a user (Configured in the Resource) to Privilege Cloud to obtain the access token that will be used in REST API calls.

To read more, please go to [REST API | CyberArk Docs](https://docs.cyberark.com/privilege-cloud-standard/latest/en/content/webservices/implementing%20privileged%20account%20security%20web%20services%20.htm?TocPath=Developers%7CREST%20APIs%7C_____0)

## Limitations

* Users filters are limited to userType, componentUser and userName
* Only Users are being manage and provisioning for the connector (for now)

## Future

* Provisioning of Groups, Accounts and Safes in the connector.

***

## Resource

### Configuration

Those attributes are used by the connector to authenticate on Privilege Cloud and generate the Access Token.

| Attribute              | Required | Default  | Description                                                |
|------------------------|----------|----------|------------------------------------------------------------|
| Server                 | YES      |          | Server address. Eg: acme.privilegecloud.cyberark.com       |
| Username               | YES      |          | The name of the user who is logging in to Privilege Cloud  |
| Password               | YES      |          | The password used by the user to log in to Privilege Cloud |
| Authentication Methods | NO       | CyberArk | This method authenticates a user to Privilege Cloud        |
| Verify SSL             | NO       | true     | Enforce SSL                                                |


### Permissions

The service user used to manage the objects on Privilege Cloud must have the following permissions:

- Audit Users
- Add Users/Update Users
- Activate User
- Reset Users Passwords 

IMPORTANT: The user who runs this Web service must be in the same Vault Location or higher as the user whose password is being reset

### Build

#### Maven

* download CyberArk Privilege Cloud connector source code from Github
* build connector with maven:
```
mvn clean install -DskipTests=true
```
* find connector-cyberark-{version}.jar in ```/target``` folder

### Installation

* put org.connid.bundles.unix-{version}.jar to ```{midPoint_home}/icf-connectors/``` or ```{midPoint_home}/connid-connectors/``` directory

### Run tests

Fill out the properties file ```src/test/resoruces/test.properties``` with your CyberArk environment:

```
domain=<host>
user=<user>
password=<password>

# Optional
# method=RADIUS
# Can be: CyberArk, LDAP or RADIUS
```

and then run:

```bash
mvn clean install
```
