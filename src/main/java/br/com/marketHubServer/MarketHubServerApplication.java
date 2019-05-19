package br.com.marketHubServer;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketHubServerApplication {
        @PostConstruct
        public void init(){
            TimeZone.setDefault(TimeZone.getTimeZone("BET"));
        }
	public static void main(String[] args) {
            SpringApplication.run(MarketHubServerApplication.class, args);
	}

}
