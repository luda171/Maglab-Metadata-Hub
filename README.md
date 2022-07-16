
# MagLab Metadata hub
MagLab Metadata	  hub is a part of the  High Magnetic Field Science Toolset (LANL Copyright No. C20099):
https://github.com/ffb-LANL/High-Magnetic-Field-Science-Toolset

### What is this repository for? ###

The Metadata hub  provides the user interface and  API to sync captured data and metadata  to OSF (Open Science Framework) repository.  


### How do I get set up? ###

* Summary of set up
``` sh
JAVA 8
with  Maven installed  to compile

mvn clean install
```
* Configuration
``` sh
edit src/main/resources/config.properties with your custom parameters

osf.callback = https://<hostname>/rest/callback
maglabfairdata.clientID = a7****d36acb8b7d2b5f28246
maglabfairdata.clientSecret = Oi****peigyMLlmcpIX61xmcNIAGuLCpCcayFdn
cal.facility=Pulsed Field

edit src/main/resources/application.properties and config.properties with
proxy settings.
``` 


* Database configuration
``` sh
application uses sqlite db
schema sql at src/main/resources/pulsefacility.sql
sample db included ./pulsefacility.db
to go to db cli:
sqlite3 pulsefacility.db
```  


* How to run 
``` sh
 nohup ./magstart.sh > out2.txt &
```
###CLENT interface ### 
*http://localhost/cal

###API###


*http://localhost/rest/


### Who do I talk to? ###

* Lyudmila Balakireva ludab@lanl.gov
* Other community or team contact
