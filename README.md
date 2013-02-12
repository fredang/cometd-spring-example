This is a small webapp to show how to use cometd with spring and tomcat.
It's part of the tutorial 
https://chimpler.wordpress.com/2013/02/11/pushing-real-time-data-to-the-browser-using-cometd-and-spring/

This webapp listens to the twitter sample firehose and pushes the status to the web page using cometd.

To deploy the application with jetty:
<pre>
  mvn jetty:run
</pre>

In your browser open the following url:
  http://localhost:8080/spring/index


To deploy the application with Tomcat:
<pre>
  mvn clean install
  cp target/cometd-spring-example-1.0.war <TOMCAT DIR>/webapps/
</pre>

Then in your browser open the following URL:
  http://localhost:8080/cometd-spring-example-1.0/spring/index
