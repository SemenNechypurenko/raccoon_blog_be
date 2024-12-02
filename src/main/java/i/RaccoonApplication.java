package i;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaccoonApplication {

    @Value("${spring.data.mongodb.uri}")
    static String mongoUri;

    public static void main(String[] args) {

        String connectingString = mongoUri;
        System.out.println("        System.out.println(connectingString): " + connectingString);

        SpringApplication.run(RaccoonApplication.class, args);
    }

}
