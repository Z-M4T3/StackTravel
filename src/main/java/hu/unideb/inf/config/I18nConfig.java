package hu.unideb.inf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Configuration
public class I18nConfig {

    @Bean
    public MessageSource messageSource(@Value("${app.locale:en}") String localeTag) {
        Locale defaultLocale = Locale.forLanguageTag(localeTag);
        if (defaultLocale.getLanguage().isBlank()) {
            defaultLocale = Locale.ENGLISH;
        }
        LocaleContextHolder.setDefaultLocale(defaultLocale);

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
