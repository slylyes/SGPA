
-- Création de la base de données
CREATE DATABASE IF NOT EXISTS pharmacie_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE pharmacie_db;


CREATE TABLE IF NOT EXISTS utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role ENUM('PHARMACIEN', 'PREPARATEUR') NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_login (login),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS medicament (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_commercial VARCHAR(200) NOT NULL,
    principe_actif VARCHAR(200) NOT NULL,
    forme_galenique VARCHAR(50) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    prix_public DECIMAL(10, 2) NOT NULL,
    necessite_ordonnance BOOLEAN DEFAULT FALSE,
    date_peremption DATE NOT NULL,
    stock_actuel INT NOT NULL DEFAULT 0,
    seuil_minimum INT NOT NULL DEFAULT 10,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nom (nom_commercial),
    INDEX idx_stock (stock_actuel),
    INDEX idx_peremption (date_peremption),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS fournisseur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    contact VARCHAR(100),
    adresse TEXT,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nom (nom),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS vente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_heure DATETIME NOT NULL,
    avec_ordonnance BOOLEAN DEFAULT FALSE,
    montant_total DECIMAL(10, 2) NOT NULL,
    id_utilisateur INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id),
    INDEX idx_date (date_heure),
    INDEX idx_utilisateur (id_utilisateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS ligne_vente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_vente INT NOT NULL,
    id_medicament INT NOT NULL,
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    sous_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_vente) REFERENCES vente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_medicament) REFERENCES medicament(id),
    INDEX idx_vente (id_vente),
    INDEX idx_medicament (id_medicament)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS commande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_fournisseur INT NOT NULL,
    date_commande DATE NOT NULL,
    date_reception DATE NULL,
    statut ENUM('EN_ATTENTE', 'RECUE', 'ANNULEE') DEFAULT 'EN_ATTENTE',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id),
    INDEX idx_statut (statut),
    INDEX idx_fournisseur (id_fournisseur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS ligne_commande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_commande INT NOT NULL,
    id_medicament INT NOT NULL,
    quantite INT NOT NULL,
    FOREIGN KEY (id_commande) REFERENCES commande(id) ON DELETE CASCADE,
    FOREIGN KEY (id_medicament) REFERENCES medicament(id),
    INDEX idx_commande (id_commande),
    INDEX idx_medicament (id_medicament)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- données de test

INSERT INTO utilisateur (login, mot_de_passe, nom, prenom, role) VALUES
('admin', 'admin123', 'Admin', 'Pharmacien', 'PHARMACIEN'),
('vendeur', 'vendeur123', 'Dupont', 'Marie', 'PREPARATEUR');

INSERT INTO fournisseur (nom, contact, adresse) VALUES
('Laboratoire Sanofi', '01 23 45 67 89', '54 Rue La Boétie, 75008 Paris'),
('Laboratoire Pfizer', '01 98 76 54 32', '23-25 Avenue du Dr Lannelongue, 75014 Paris'),
('Cooper Pharmaceutique', '04 72 00 00 00', '1 Place Lucien Renaud, 69008 Lyon'),
('Biogaran', '01 55 72 60 00', '15 Boulevard Charles de Gaulle, 92700 Colombes');

INSERT INTO medicament (nom_commercial, principe_actif, forme_galenique, dosage, prix_public, 
                       necessite_ordonnance, date_peremption, stock_actuel, seuil_minimum) VALUES
('Doliprane', 'Paracétamol', 'Comprimé', '1000mg', 3.50, FALSE, '2026-12-31', 150, 50),
('Ibuprofène', 'Ibuprofène', 'Comprimé', '400mg', 2.80, FALSE, '2026-10-15', 200, 50),
('Aspegic', 'Acide acétylsalicylique', 'Poudre', '500mg', 4.20, FALSE, '2027-06-30', 80, 30),
('Spasfon', 'Phloroglucinol', 'Comprimé', '80mg', 5.50, FALSE, '2026-08-20', 120, 40),
('Humex Rhume', 'Paracétamol/Pseudoéphédrine', 'Comprimé', '500mg/60mg', 6.30, FALSE, '2026-11-10', 90, 30),
('Gaviscon', 'Alginate de sodium', 'Suspension', '10ml', 7.80, FALSE, '2026-09-15', 60, 20),
('Smecta', 'Diosmectite', 'Poudre', '3g', 4.90, FALSE, '2027-03-25', 100, 30),
('Vicks Vaporub', 'Camphre/Eucalyptus', 'Pommade', '50g', 8.50, FALSE, '2027-01-10', 45, 15),

-- Médicaments avec ordonnance
('Amoxicilline', 'Amoxicilline', 'Gélule', '500mg', 8.90, TRUE, '2026-07-20', 70, 25),
('Augmentin', 'Amoxicilline/Acide clavulanique', 'Comprimé', '1g/125mg', 12.50, TRUE, '2026-09-05', 50, 20),
('Azithromycine', 'Azithromycine', 'Comprimé', '250mg', 15.30, TRUE, '2026-10-30', 40, 15),
('Levothyrox', 'Lévothyroxine', 'Comprimé', '75µg', 6.20, TRUE, '2027-02-15', 80, 30),
('Kardégic', 'Acide acétylsalicylique', 'Poudre', '75mg', 3.80, TRUE, '2026-12-20', 95, 35),
('Inexium', 'Esoméprazole', 'Comprimé', '20mg', 18.50, TRUE, '2026-11-25', 30, 15),
('Ventoline', 'Salbutamol', 'Aérosol', '100µg', 5.90, TRUE, '2026-08-30', 25, 10),
('Doliprane Codéiné', 'Paracétamol/Codéine', 'Comprimé', '500mg/25mg', 9.50, TRUE, '2026-10-10', 60, 20),

('Biafine', 'Trolamine', 'Crème', '93g', 11.20, FALSE, '2026-12-31', 8, 15),
('Oracéfal', 'Céfadroxil', 'Gélule', '500mg', 14.80, TRUE, '2026-09-20', 5, 10),

('Pansement Cicatryl', 'Bactéricide', 'Crème', '30g', 6.50, FALSE, '2026-04-15', 40, 10),
('Maxilase', 'Alpha-amylase', 'Comprimé', '3000 U', 7.90, FALSE, '2026-03-10', 35, 15);

INSERT INTO vente (date_heure, avec_ordonnance, montant_total, id_utilisateur) VALUES
('2026-02-01 10:30:00', FALSE, 23.90, 2),
('2026-02-02 14:15:00', TRUE, 42.20, 2),
('2026-02-03 09:45:00', FALSE, 15.40, 2),
('2026-02-04 16:20:00', TRUE, 31.50, 1),
('2026-02-05 11:00:00', FALSE, 23.70, 2);

INSERT INTO ligne_vente (id_vente, id_medicament, quantite, prix_unitaire, sous_total) VALUES
-- Vente 1
(1, 1, 2, 3.50, 7.00),
(1, 2, 1, 2.80, 2.80),
(1, 5, 1, 6.30, 6.30),
(1, 6, 1, 7.80, 7.80),
-- Vente 2
(2, 9, 2, 8.90, 17.80),
(2, 14, 1, 18.50, 18.50),
(2, 15, 1, 5.90, 5.90),
-- Vente 3
(3, 1, 3, 3.50, 10.50),
(3, 7, 1, 4.90, 4.90),
-- Vente 4
(4, 10, 1, 12.50, 12.50),
(4, 16, 2, 9.50, 19.00),
-- Vente 5
(5, 4, 2, 5.50, 11.00),
(5, 3, 1, 4.20, 4.20),
(5, 8, 1, 8.50, 8.50);

INSERT INTO commande (id_fournisseur, date_commande, statut) VALUES
(1, '2026-02-01', 'EN_ATTENTE'),
(2, '2026-01-25', 'RECUE'),
(3, '2026-02-05', 'EN_ATTENTE');

INSERT INTO ligne_commande (id_commande, id_medicament, quantite) VALUES
-- Commande 1 (en attente)
(1, 18, 20),
(1, 19, 15),
-- Commande 2 (reçue)
(2, 1, 100),
(2, 2, 150),
-- Commande 3 (en attente)
(3, 15, 30),
(3, 16, 40);
