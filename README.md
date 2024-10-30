# CyberArk Privilege Cloud Connector

Connector to provisioning Users and Groups for CyberArk Privilege Cloud.

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

* Users filters are limited to the API, only for userType, componentUser and userName
* Only Users are being manage and provisioning for the connector

## Future

* Provisioning of Groups, Accounts and Safes in the connector.

***

