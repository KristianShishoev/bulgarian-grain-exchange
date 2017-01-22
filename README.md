# bulgarian-grain-exchange

A JavaEE website, that makes available users to buy and sell grain also offers useful information about grain prices.

# Running application

The following instructions are for running application on Windows, Wildfly application server and MySQL database:

1. Download WildFly 10 (http://wildfly.org/downloads/) and extract it somewhere.

2. Download and install My SQL Server (http://dev.mysql.com/downloads/mysql/)

3. Download the MySQL connector jar (https://dev.mysql.com/downloads/connector/j/). You are going to use it later when you create a JDBC data source in WildFly.

4. Run MySQL and create user and database. Example user: root pass: admin database: bgx

5. Create managment user for WildFly server (https://www.youtube.com/watch?v=8HUFaL--SwI)

6. Run Wildfly server WildFlyDirectory/bin/standalone.bat

7. Follow this youtube video (https://www.youtube.com/watch?v=xSHXMcRsF0A) to setup datasource. Note that jndi name MUST be java:/bgxDS .

8. mvn clean install will build and deploy application automatic.

9. Open the web app at http://localhost:8080/bulgarian-grain-exchange

10. To debug application you need to start wildfly with command: standalone.bat --debug --server-config=standalone.xml and then connect eclipse
debugger on 8787 port as remote java application.

11. To setup mail api on wildfly follow following tutorial (for testing purposes gmail it is used): http://khozzy.blogspot.bg/2013/10/how-to-send-mails-from-jboss-wildfly.html 