package com.snhu.sslserver;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.catalina.Context;


@SpringBootApplication
public class SslServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SslServerApplication.class, args);
	}
	
	@Bean
	public ServletWebServerFactory servletContainer() {
	    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
	        @Override
	        protected void postProcessContext(Context context) {
	            SecurityConstraint securityConstraint = new SecurityConstraint();
	            securityConstraint.setUserConstraint("CONFIDENTIAL");
	            SecurityCollection collection = new SecurityCollection();
	            collection.addPattern("/*");
	            securityConstraint.addCollection(collection);
	            context.addConstraint(securityConstraint);
	        }
	    };
	    tomcat.addAdditionalTomcatConnectors(redirectConnector());
	    return tomcat;
	}

	private Connector redirectConnector() {
	  Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
	  connector.setScheme("http");
	  connector.setPort(8888);
	  connector.setSecure(false);
	  connector.setRedirectPort(8444);
	  
	  return connector;
	}

}

//FIXME: Add route to enable check sum return of static data example:  String data = "Hello World Check Sum!";
@RestController
class ServerController_2{
//FIXME:  Add hash function to return the checksum value for the data string that should contain your name. 
	public static String getHash(String name) throws NoSuchAlgorithmException 
    {  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
        byte[] hash =  md.digest(name.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hash);  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
        while (hexString.length() < 32)  
        {  
            hexString.insert(0, '0');  
        }  
        return hexString.toString();  
     
    } 
    @RequestMapping("/getAlgorithmHash")
    public String algorithm(){
    	
    	String data = "Ray Sauter";
    	String hash = "";
    	try {
			hash = getHash(data);
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
       
    	return "<h3>Data</h3>"+data+" : <h3>Used Hash </h3> <br>SHA-256 <h3>"+"Result:  "+hash+"</h3>";
    }
}
