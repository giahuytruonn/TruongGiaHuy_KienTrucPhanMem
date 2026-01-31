package iuh.fit.bai3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Bai3Application {

    public static void main(String[] args) {
        io.github.cdimascio.dotenv.Dotenv dotenv =
                io.github.cdimascio.dotenv.Dotenv.configure()
                        .ignoreIfMissing()
                        .load();

        dotenv.entries().forEach(e ->
                System.setProperty(e.getKey(), e.getValue())
        );
        SpringApplication.run(Bai3Application.class, args);
    }

}
