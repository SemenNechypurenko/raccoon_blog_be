package i;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaccoonApplication {

    public static void main(String[] args) {
        System.out.println("---------------------------------------------------------");
        // Вывод значения переменной окружения
        String mongoUri = System.getenv("SPRING_DATA_MONGODB_URI");
        System.out.println("SPRING_DATA_MONGODB_URI: " + (mongoUri != null ? mongoUri : "Переменная не найдена"));
        SpringApplication.run(RaccoonApplication.class, args);
    }

}
