# Java DAO Factory · Filmographie

Projet académique réalisé dans le cadre du cours de SGBD. Cette application console illustre l’accès aux données avec Java et deux moteurs de bases de données. Elle est conçue comme exercice pédagogique.

## Fonctionnalités

- Créer, consulter, modifier, supprimer et lister les films et les acteurs.
- Associer plusieurs acteurs à un film, retirer une association et afficher la distribution.
- Choisir MySQL ou MongoDB au lancement avec les mêmes interfaces Java.

## Architecture

Le pattern **DAO** sépare les modèles `Film` et `Acteur` de leur persistance. `IDao<T>` définit le CRUD et le listage. `DaoActeur` et `DaoFilm` spécialisent ce contrat, et `DaoFilm` expose la gestion des associations.

Le pattern **Abstract Factory** fournit une famille cohérente de DAO par moteur. `DaoFactory.getDaoFactory(type)` sélectionne `MySQLFactory` ou `MongoFactory`. Le point d’entrée utilise uniquement la fabrique abstraite et les contrats DAO.

| | MySQL | MongoDB |
| --- | --- | --- |
| Accès | JDBC et requêtes préparées | Pilote Java synchrone et documents BSON |
| Stockage | Tables `Film`, `Acteur`, `Joue` | Collections `Films`, `Acteurs`, `Compteurs` |
| Associations | Table de jonction, clés étrangères et suppression en cascade | Tableau `acteurs_ids`, contrôle et nettoyage dans les DAO |
| Identifiants | Clés auto-incrémentées | Compteur atomique par collection au-dessus des identifiants existants |

Dans MongoDB, les opérations sur plusieurs documents s’exécutent séparément pour rester compatibles avec un serveur autonome. Si le nettoyage des liens échoue après la suppression d’un acteur, relancer la suppression permet de reprendre le nettoyage. Les dates de naissance sont enregistrées à minuit UTC. Les anciennes dates BSON et les chaînes ISO restent lisibles. Les associations se gèrent via les méthodes de `DaoFilm`, indépendamment de la modification du titre ou de l’année.

## Installation et configuration

Prérequis — JDK 17 ou supérieur, Maven 3, et au moins un serveur local avec son client en ligne de commande. Le projet utilise MySQL Connector/J, le pilote MongoDB Sync et SLF4J Simple.

```powershell
git clone https://github.com/youba-a/Java-DAO-Factory.git
Set-Location Java-DAO-Factory
mvn clean verify
```

Les scripts créent les structures manquantes et préservent les données existantes. Une base neuve commence vide. Utilisez un compte dédié à la base `Filmographie` pour l’exécution. Conservez les identifiants dans l’environnement local de votre session.

### MySQL

Depuis la racine du projet, ouvrez le client MySQL avec un compte autorisé à créer le schéma, puis chargez le script.

```text
mysql -u <compte-initialisation> -p
mysql> source database.sql
```

Configurez ensuite la session PowerShell avec le compte applicatif.

```powershell
$env:FILM_MYSQL_URL = 'jdbc:mysql://127.0.0.1:3306/Filmographie'
$env:FILM_MYSQL_USER = Read-Host 'Utilisateur MySQL'
$env:FILM_MYSQL_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Mot de passe MySQL' -AsSecureString)).Password
```

L’URL ci-dessus est utilisée par défaut. L’utilisateur et le mot de passe doivent être configurés.

### MongoDB

```powershell
$env:FILM_MONGO_DATABASE = 'Filmographie'
mongosh --file mongo_init.js
```

Sans configuration, Java se connecte à `mongodb://127.0.0.1:27017` et sélectionne `Filmographie`. Pour un serveur avec authentification ou une autre adresse, renseignez `FILM_MONGO_URI` dans la session locale et utilisez le même serveur pour `mongosh`.

## Vérifications

`mvn clean verify` compile et empaquette le projet. Le scénario d’intégration s’exécute séparément pour chaque moteur disponible.

```powershell
mvn org.codehaus.mojo:exec-maven-plugin:3.1.0:java '-Dexec.mainClass=Main' '-Dexec.args=MYSQL'
mvn org.codehaus.mojo:exec-maven-plugin:3.1.0:java '-Dexec.mainClass=Main' '-Dexec.args=MONGODB'
```

Le scénario vérifie le CRUD, le listage, la date de naissance, l’ajout idempotent d’un lien, son retrait et le nettoyage des liens à la suppression d’un acteur. Il travaille avec ses propres enregistrements temporaires et remonte un échec si une vérification échoue.
