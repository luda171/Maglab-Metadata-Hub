
# MagLab Metadata hub
MagLab Metadata	  hub is a part of the  High Magnetic Field Science Toolset (LANL Copyright No. C20099):
https://github.com/ffb-LANL/High-Magnetic-Field-Science-Toolset

### What is this repository for? ###

The Maglab Metadata Hub maintains the metadata associated with the Maglab users' projects.  The actual framework architecture consists of 3 independent parts – a server (Hub) which contains the user projects info, a data acquisition client module to read the project info from the Hub, and an optional library to push the acquired experimental data and metadata to the Open Science Framework (OSF). The Hub provides the GUI web interface and the API to access the project metadata and to synchronize the captured data and metadata to the OSF repository.


### How to get install and set up the Hub ###

* Instalation summary 
``` sh
** consult with linux_install.readme.md for red hat linux example of java, maven, and nginx install. 
** install JAVA 8
** compile souce code with Maven

```
* Register your application with OSF
``` sh
login to OSF
go to Developer APPS tab and click "Create Developer APP button":

```
![alt text](https://github.com/luda171/Maglab-Metadata-Hub/blob/master/img/reg_osf0.png?raw=true)
``` sh
and enter your server information:
```
![alt text](https://github.com/luda171/Maglab-Metadata-Hub/blob/master/img/reg_osf1.png?raw=true)

* Edit configuration
``` sh
edit src/main/resources/config.properties with your custom parameters:

osf.callback = https://<hostname>/rest/callback
maglabfairdata.clientID = a7****d36acb8b7d2b5f28246
maglabfairdata.clientSecret = Oi****peigyMLlmcpIX61xmcNIAGuLCpCcayFdn
cal.facility=Pulsed Field
(for all records, specify cal.facility=all )
edit src/main/resources/application.properties and config.properties with
proxy settings.
``` 
* Compile
``` sh
mvn clean install
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
change permissions to executable
chmod +x start.sh
nohup ./start.sh > out2.txt &
see  linux_install.readme.md how to config application to run as service on linux.
```
### Client GUI interface  for the list of experiments ### 
*http://localhost:8085/cal

## HUB API ##

Base url for API:  http://localhost:8085/rest/

* Gives current  experiment for the station   in "application/json" format
* https://localhost:8085/rest/now/[station]

	
``` sh
example
GET  https://localhost:8085/rest/now/Cell_1

[
   {
      "title":"Interfacial Superconductivity in Bi2Te3/FeTe Heterostructures under High Magnetic Fields",
      "magnet_System":"65 T Multi shot 25 mS (Short Pulse)",
      "proposal_Number":"P19621",
      "proposal_Title":"Interfacial Superconductivity in Bi2Te3/FeTe Heterostructures under High Magnetic Fields",
      "facility":"Pulsed Field",
      "pi":"Cui-Zu Chang",
      "dtstart":"2022-12-05",
      "dtend":"2022-12-10",
      "dtstamp":"2022-12-05",
      "location":"Cell_1",
      "summary":"P19621-E004-PF; PI: Cui-Zu Chang; Support: John Singleton, Laurel Winter",
      "pid":"P19621-E004-PF",
      "support":"John Singleton, Laurel Winter",
      "id":42497,
      "start":1670223600000
   }
]
  
```
* Gives current  experiment for the station with specified start date in json format
* https://localhost:8085/rest/[date]/[station]
 ``` sh
example
GET  https://localhost:8085/rest/20210608/Cell_4
```
## OSF syncronization

*  Initiates a osf form that asks the user to grant authorization for application to sync data. 
``` sh
GET  https://localhost:8085/rest/auth?expid=P19635-E002-PF&station=Cell_4
```
*  Initiates a logoff of user from osf
``` sh
GET http://localhost:8085/rest/logoff?expid=P19635-E002-PF&station=Cell_4
```
* Check user authorization status
``` sh
GET https://localhost:8085/rest/status?expid=P19635-E002-PF&station=Cell_4
```
* Submit file stream
``` sh
PUT https://hostname:8085/rest/updatefile?name=p004_113021.tdms&expid=P19635-E002-PF&station=Cell_4
``` 
* Submit wiki update for current  experiment for the station  
``` sh
PUT https://hostname:8085/rest/updatewiki?name=p004_113021.tdms&expid=P19635-E002-PF&station=Cell_4
``` 



A LabVIEW client module example can be found at https://github.com/ffb-LANL/NHMFL_Core/blob/master/Libraries/FAIR%20Data/MagLab%20Hub%20Client.vi

### Who do I talk to? ###

* Lyudmila Balakireva ludab@lanl.gov

