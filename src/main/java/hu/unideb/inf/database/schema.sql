CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    pass VARCHAR(255) NOT NULL,
    birth_date DATE NULL,
    picture VARCHAR(255) NULL,
    last_email VARCHAR(255) NULL,
    last_email_change TIMESTAMP NULL DEFAULT NULL,
    last_login TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS trips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    public_key VARCHAR(255) NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS trip_users (
    user_id INT NOT NULL,
    trip_id INT NOT NULL,
    permission_level VARCHAR(255) NOT NULL,
    CONSTRAINT fk_trip_users_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_trip_users_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, trip_id)
);

CREATE TABLE IF NOT EXISTS categories (
    name VARCHAR(255) PRIMARY KEY,
    photo VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    category VARCHAR(255) NULL,
    location VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    webpage VARCHAR(255) NULL,
    duration VARCHAR(255) NULL,
    picture VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cards_category FOREIGN KEY (category) REFERENCES categories(name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trip_cards(
    trip_id INT NOT NULL,
    card_id INT NOT NULL,
    card_day INT NOT NULL,
    start_time DATE NULL,
    duration INT NULL,
    CONSTRAINT fk_trip_cards_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    CONSTRAINT fk_trip_cards_card_id FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
    PRIMARY KEY (trip_id, card_id)
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    token_hash CHAR(64) PRIMARY KEY,
    user_id INT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_tokens_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);