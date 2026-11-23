-- Initialisation du schéma. Les tables et données existantes sont conservées.
CREATE DATABASE IF NOT EXISTS Filmographie CHARACTER SET utf8mb4;
USE Filmographie;

CREATE TABLE IF NOT EXISTS Acteur (
    idA INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    dnaiss DATE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Film (
    idF INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(200) NOT NULL,
    annee INT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Joue (
    idA INT,
    idF INT,
    PRIMARY KEY(idA, idF),
    FOREIGN KEY(idA) REFERENCES Acteur(idA) ON DELETE CASCADE,
    FOREIGN KEY(idF) REFERENCES Film(idF) ON DELETE CASCADE
) ENGINE=InnoDB;
