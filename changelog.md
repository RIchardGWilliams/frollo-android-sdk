## Changelog

### 3.18.0

#### Changes
- AccountCreateUpdateRequest - Replace account type with account sub type
- Dynamic IV for KeyStore
- List Similar Transactions API
- Update Bulk Transactions API
- Quick Sync API

### 3.17.1

#### Changes
- Assets - Remove frequency field from assets breakdown
- Liabilities - Rename frequency in liabilities breakdown
- Remove support for Android 6 & 7

### 3.17.0

#### Changes
- Manual Account - Create, Update, Delete
- Osko Service Types in Payment & Transaction APIs
- Add pagination to Consent API Response
- Change sharing started, stopped & expired dates to ISO8601 datetime format in Consent API Response
- Add new fields to Contact API Response
- Add new Payment Methods - digital_wallet & card
- Changes to Financial Passport Query & Response

### 3.16.0

#### Changes
- Add new aggregator type data_action
- Update Account Groups, Account Containers and Account Types

### 3.15.2

#### Changes
- Passwordless Authentication Tokens API
- Fix Dokka Documentation Issue

### 3.15.1

#### Changes
- Fix DB Migration Issue

### 3.15.0

#### Changes
- Do not pass name, dateOfBirth, email, mobileNumber, gender fields while submitting KYC.
- Add middle_names field to User model
- Add X-Host header to Service Status APIs
- Update CDR Config APIs
- Remove Tax info from User model, Add Tax info to ManagedProductCreateRequest
- Refactor logging to delegate responsibility to the client application implementing the SDK

### 3.14.0

#### Changes
- Fix Payday API field name

### 3.13.0

#### Changes
- DB encryption support

### 3.12.0

#### Features
- Financial Passport

#### Changes
- Update Host API version to v2.16
- Support DA OAuth2 Login with PKCE Authorization Code Flow
- Add availability field to Managed Product API response

### 3.11.1

#### Changes

- Added overrideMethod in PayAnyoneRequest to force payment through DE route

### 3.11.0

#### Changes

- Fix OAuth2Error description
- Consents - Change additional_permissions to string array instead of JsonObject

### 3.10.0

#### Features
- Statements
- Service Status & Outage APIs

#### Changes

- Updated to support JDK 11
- Updated koin version to 3.0.1
- Removed jCenter dependency and added aar files temporarily in libs folder
- Verify BPay
- Payment Limits APIs
- Add new payment error codes
- Fix sync provider accounts API query parameter
- Update Host API version to v2.15
- Add Associated PayIDs to account response
- Added new card statuses
- Add reference and rejected reason to transaction and additional transaction statuses
- Add support for digital wallets in cards
- Add Session ID in Header
- Provider API response changes to support joint accounts
- Add features array to Managed Products API response
- Optional first name during user registration
- Additional user statuses - CREATED, FAILED and INVITED
- Changes to Address API response

### 3.9.0

#### Features

- Payday
- Address APIs

#### Changes

- Surveys - Support Freeform & Numeric types
- User Feedback convenience method
- Support custom OAuth Audience & Grant Type
- Verify BSB support
- Add email to user unconfirmed details response
- User date of birth date format change from yyyy-MM to yyyy-MM-dd
- Replace permissions object cluster with permission IDs list in Consent and Provider models
- Add permissions object cluster to CDR Configuration model
- Add external_id to User model
- Remove addresses from UserKYC model
- Update address fields in User model
- Update Host API version to v2.14

### 3.8.0

#### Features

- Managed Products
- Cards

#### Changes

- Increase HttpClient Read, Write, Connect timeout to 30 seconds
- Add domain parameter to revoke token API
- Expose logger to the client app to help the app to log any errors to the host
- Add API error code and message to API Error Descriptions

### 3.7.0

#### Features

- PayID - Create & Manage
- Payments via PayID
- KYC

#### Changes

- Request OTP and verify user mobile number
- Update Host API version to v2.13

### 3.6.0

#### Features

- Images
- Payments
- Consents

#### Changes

- Optimize Transactions advanced filtering and searching
- Add Account Features & CDR Product Information to Account API Response
- Migrate from Application Context to ContentProvider Context
- Added Fetch methods that return Rx Observable
- Provider availability status
- Add dueAmount parameter to Create Bill method
- Add billID filter to Transaction API
- Add Budget Current Period Ready notification event
- Add DEMO AggregatorType
- Add goalID filter to Transaction API and Transaction model
- Return Budget ID after budget creation
- Return Goal ID after goal creation
- Add CDR permissions data cluster to Provider Model
- Add OTP Header to Make Payment API requests
- Upgrade Host API Version to 2.12
- User Register Steps, new Address model and Tax Information

### 3.5.0

#### Features

- Transactions advanced filtering and searching

#### Changes

- Transaction pagination improvements

### 3.4.0

#### Features

- Generic metadata support and additional properties for surveys
- Refactor and Remodel Transaction Reports
- Merchants Pagination
- Budgets & Budget Periods

#### Changes

- Update Frollo Host API version to 2.7
- Add properties - 'aggregator_type' & 'permissions' to Provider model

### 3.3.0

#### Features

- Force a refresh of a provider account with the aggregator
- Refresh cached merchant data
- Goals enhancements and generic metadata support
- Messages generic metadata support

#### Changes

- Update Frollo Host API version to 2.6
- Send app version in addition to SDK version in headers
- Messages supports expanded URL opening methods

### 3.2.0

#### Changes
- Added support to show reports based on tag.

### 3.1.0

#### Changes
- Added support to change a transactions budget category and to apply it to all similar transactions

### 3.0.0

#### Changes
- Improved custom authentication making it easier to implement
- Add externalId for mapping external aggregator ID for ProviderAccounts, Accounts & Transactions
- Allow merchant image to be optional

### 2.1.0

#### Changes
- Goals
- Handle requests on token expiry and cancel queued requests on reset
- Add method for initializing authentication callbacks for custom authentication

#### Features
- Goals
- Update Frollo Host API version to 2.4

### 2.0.5

#### Changes
- Update Account - Update cache before firing the network request

### 2.0.4

#### Changes
- Bug Fix - Do not delete DISABLED & UNSUPPORTED providers from database while saving response from refresh providers API

### 2.0.3

#### Changes
- Bug Fix - Revert back to use Frollo Host API v2.2. Will update to Frollo Host API v2.4 in a future version.

### 2.0.2

#### Changes
- Add Client ID to User Register & User (Password) Reset API requests

### 2.0.1

#### Changes
- Token Injection (cherrypicked from 1.3.4)
- Bug Fix - Token Expiry Calculation (cherrypicked from 1.3.4)

### 2.0.0
- Custom Authentication - Custom handling of authentication can be provided to the SDK

#### Features
- Custom Authentication

### 1.4.4

#### Changes
- Bug Fix - Revert back to use Frollo Host API v2.2. Will update to Frollo Host API v2.4 in a future version.

### 1.4.3

#### Changes
- Token Injection (cherrypicked from 1.3.4)
- Bug Fix - Token Expiry Calculation (cherrypicked from 1.3.4)

### 1.4.2

#### Changes
- Update Frollo Host API version to 2.4 to support the refactored Account Balance Reports API.

### 1.4.1

#### Changes
- Refactor Aggregation - createProviderAccount to return the ID of the provider account created.

### 1.4.0

#### Changes
- Update Tagging APIs & methods
- Enhanced methods for Aggregation, Reports, Surveys, Messages
- Improved handling of logged out status

### 1.3.4

#### Changes
- Token Injection
- Bug Fix - Token Expiry Calculation

### 1.3.3

#### Changes
- Logout calls OAuth2 revoke token API

### 1.3.2

#### Changes
- Migrate user to new identity provider

### 1.3.1

#### Changes
- Allow custom parameters in authorization code flow

### 1.3.0
- Bills
- Enhanced Transaction - with Merchant location & phone
- Support for fetching latest published survey
- Auto dismiss flag for Messages
- Transaction tags

#### Features
- Bills
- Bill Payments
- Enriched merchant details on a Transaction

### 1.2.0
- Reports

#### Features
- Account Balance Reports
- Current Transaction Reports
- History Transaction Reports

### 1.1.0
- Surveys

#### Features
- Surveys

### 1.0.0
- Initial release

#### Features
- OAuth2 Authentication
- Aggregation
- Events
- Messages
- Notifications
- User