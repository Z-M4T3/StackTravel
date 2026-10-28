package hu.unideb.inf;

import hu.unideb.inf.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        DotenvConfig.load();
        SpringApplication.run(Main.class, args);
    }
}