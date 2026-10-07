package hu.unideb.inf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(Environment environment) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost(environment.getRequiredProperty("spring.mail.host"));
        sender.setPort(environment.getProperty("spring.mail.port", Integer.class, 587));
        sender.setUsername(environment.getRequiredProperty("spring.mail.username"));
        sender.setPassword(environment.getRequiredProperty("spring.mail.password"));
        sender.setProtocol("smtp");
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        sender.getJavaMailProperties().put("mail.smtp.auth", "true");
        sender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
        sender.getJavaMailProperties().put("mail.smtp.connectiontimeout", "5000");
        sender.getJavaMailProperties().put("mail.smtp.timeout", "3000");
        sender.getJavaMailProperties().put("mail.smtp.writetimeout", "5000");

        return sender;
    }
}
