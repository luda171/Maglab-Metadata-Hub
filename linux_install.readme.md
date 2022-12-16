## Step 1: Install linux packages 
``` sh
sudo yum install nginx emacs java-1.8.0-openjdk-devel git
You can run java -version command to verify the JDK installation.
java -version
readlink -f $(which java)
```
## Step 2: Download the Maven Binaries
``` sh
wget https://mirrors.estointernet.in/apache/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz 
tar -xvf apache-maven-3.6.3-bin.tar.gz
sudo mv apache-maven-3.8.5 /opt/
```
## Step 3: Setting JAVA_HOME and maven home and Path Environment Variables
``` sh
emacs .bash_profile
add to profile your variables.
export M2_HOME='/opt/apache-maven-3.8.5'
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.352.b08-2.el8_7.x86_64/jre/
PATH="$M2_HOME/bin:"$JAVA_HOME/bin:$PATH"                                                                                                                                                                                                                           
export PATH  
```
You can relaunch the terminal or execute source .bash_profile command to apply the configuration changes.
Verify the Maven installation
``` sh
mvn -version
```
if you have to work with proxy
add settings.xml file at user directory  ~/.m2 directory with content

``` sh
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <proxies>
    <proxy>
      <id>myproxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>proxy1.lanl.gov</host>
      <port>8080</port>
    </proxy>
  </proxies>
</settings>
```
## Step 4:  Create directory for project
``` sh
sudo mkdir /data
sudo chown -R ludab:users /data 
cd /data
mkdir web
sudo chown -R ludab:users web
cd web
git clone https://github.com/luda171/Maglab-Metadata-Hub/
```
## Step 5 Nginx config
``` sh
copy /data/web/Maglab-Metadata-Hub/magx.conf to /etc/nginx/conf.d
Adjust server_name, directories to your own
cp /data/web/Maglab-Metadata-Hub/magx.conf /etc/nginx/conf.d
```
### start nginx
``` sh
sudo service nginx start
```
## Folow readme to config application 
## Security settings new server
``` sh
if server has selinux enabled, 

> getenforce
> Enforcing
> sestatus
SELinux status:                 enabled
SELinuxfs mount:                /sys/fs/selinux
SELinux root directory:         /etc/selinux
Loaded policy name:             targeted
Current mode:                   enforcing
Mode from config file:          enforcing
Policy MLS status:              enabled
Policy deny_unknown status:     allowed
Memory protection checking:     actual (secure)
Max kernel policy version:      33

> sudo setsebool -P httpd_can_network_connect true
You can also open the required ports for a service by using the –add-service option:

> sudo firewall-cmd --zone=public  --permanent --add-service=http
> sudo firewall-cmd --zone=public  --permanent --add-service=https
> sudo firewall-cmd --list-all
```
### Configuring automatic start up if server rebooted
``` sh
 add nginx to start after reboot 
 > sudo chkconfig nginx on
```
add maghub service 
``` sh
create file as root user
emacs  /etc/systemd/system/maghub.service
with following content (adjust username and directories:
Description=Maghub Server Service
After=network.target

[Service]
User=ludab
WorkingDirectory=/data/web/Maglab-Metadata-Hub/
ExecStart=/bin/bash -c "/data/web/Maglab-Metadata-Hub/start.sh > /data/web/Maglab-Metadata-Hub/logs/maghub.log"
ExecStop=/bin/kill -15 $MAINPID
Restart=always

```
append: option for loging  has been introduced in systemd version 240.  Check  man systemd.exec if you have it.  Otherwise use other Syslog methods.



