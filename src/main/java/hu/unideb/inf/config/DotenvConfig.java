package hu.unideb.inf.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class DotenvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private DotenvConfig() {}

    public static void load() {
        dotenv.entries().forEach(entry -> {
            if (System.getProperty(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}