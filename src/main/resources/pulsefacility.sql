#sqlite3 pulsefacility.db 

create table experiments(
experimentID INTEGER,
PI VARCHAR(30) NOT NULL,
location VARCHAR(10) NOT NULL,
facility VARCHAR(20) NOT NULL,
magnetsystem VARCHAR(50) NOT NULL,
experiment_title VARCHAR(80) NOT NULL,
proposal_title VARCHAR(200) NOT NULL,
proposal_number VARCHAR(20) NOT NULL,
dtstart TEXT,
dtend  TEXT,
dtstamp TEXT,
summary  VARCHAR(80),
support   VARCHAR(200),
dtupdate TEXT,
pid VARCHAR (20) NOT NULL,
calID VARCHAR(30),
localupdate varchar(1)

)
#Alter table experiments add  calID VARCHAR(30);
#Alter table experiments add  localupdate varchar(1);

CREATE UNIQUE INDEX idx_pid_dtstart ON experiments (pid,dtstart);
CREATE UNIQUE INDEX idx_calid ON experiments (calID);
create table results(
pid   VARCHAR(20),
filename VARCHAR(100),
experimentID INTEGER,
metadata text)
CREATE  INDEX idx_pid ON results (pid);

create table osf_user_access_log(
pid  VARCHAR(20),
access_token VARCHAR(50),
osf_name VARCHAR(30),
dtgranted TEXT,
expire_in  TEXT,
status TEXT,
projnode VARCHAR(15),
expnode VARCHAR(15),
wikinode VARCHAR(15),
refresh_token VARCHAR(50),
location VARCHAR(10) 
)
CREATE TABLE IF NOT EXISTS instruments (
		 instrument_pid VARCHAR(36) NOT NULL,
                 title  TEXT NOT NULL, 
                 filename TEXT,
                 filestore_path  TEXT NOT NULL
                 create_date TEXT,
                 modify_date TEXT,
	             out_of_service VARCHAR(1),
	             instrument_type VARCHAR(50)
			);

#Alter table osf_user_access_log add   refresh_token VARCHAR(50);
#Alter table osf_user_access_log add   location VARCHAR(10);
#.headers ON
create view dedupexperiments as select experimentID,PI,location,facility,magnetsystem,experiment_title, proposal_title,proposal_number,max(dtstart) as dtstart,dtend,dtstamp,summary,support,dtupdate,pid,calID,localupdate from experiments group by pid;
