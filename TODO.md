Auth2 TODO list
===============

Update core services and SDK modules
------------------------------------
*Note:* not in auth team scope unless specified

Need to use updated server stubs & auth clients, use tokens for magic user
accounts and tests if any, allow setting auth service url

* Shock & Awe - Rich
* Perl auth & server stubs - Keith
* Handle service & manager - Keith
* Narrative (Login UI and Lua) - Bill R. (external to auth team)
  * Token cleanup - remove backup token, make simple token format
* kb_sdk
  * Tests support token vs uid/pwd & setting auth url
  * Recompile & test all SDK modules
* NJSW
* User Profile
  * Update to get and set user name & email from and to auth service
* Service wizard
* Narrative Method Store
* Data Import Export
* Search
* Solar auth
* Bulk IO

Auth service work
-----------------
* UI (1-2 sprints w/ 1-2 FTEs per Bill & Erik)
  * Probably means altering server endpoints in concert with UI development
* 200 TODOs in the codebase on average
* Read through all prototype code and convert to production worthy
* A code review by Steve Chan wouldn't be a bad idea
* Code analysis:
  * https://www.codacy.com/
  * https://find-sec-bugs.github.io/
* Tests
  * With mock services for globus and google
* Documentation
  * Code documentation
  * User documentation and education (probably need doc team help here)
  * Login & signup very different
* General
  * Password strength checker, check passwords != on reset
  * Lock local account for X m after Y failed logins
  * Check user input for obscene or offensive content and reject, find 3rd party code (?)
* Logging for all methods - at least log user and action
* Deploy
  * Dockerization
* Memory based data storage
* Test mode
  * test apis for user creation & admin
  * auto configure server for ease of use

External dependencies
---------------------
* JGI stops using uid/pwd to login for jgidm account

Future work
-----------

### 3rd party developers acting on behalf of users (e.g. JGI, sequencing centers)
* OAuth2 endpoint
* -or-
* (simpler) verify user name via KBase login

### Scoped tokens
* Mainly for SDK jobs
* Scoped to read/write specific workspaces only, no other system rights
* Could be scoped for other things if necessary
