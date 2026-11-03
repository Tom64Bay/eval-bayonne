# Library API

API REST de gestion de bibliotheque (auteurs, livres, adherents, prets), construite avec Spring Boot.

Ce projet sert de **base de reference saine** pour des entretiens techniques : une future merge request
(contenant des erreurs, anti-patterns ou regressions volontaires) sera creee a partir de cette base pour
etre analysee et revue par les candidats.

## Stack technique

- Java 17
- Spring Boot 3.3 (Web, Data JPA, Validation, Actuator)
- Hibernate / Spring Data JPA
- PostgreSQL (dev / prod) + Flyway pour les migrations de schema
- H2 en memoire pour les tests
- Lombok
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, AssertJ, MockMvc

## Domaine fonctionnel

- **Author** (auteur) : 1 auteur -> N livres
- **Book** (livre) : rattache a un auteur, peut avoir plusieurs prets dans le temps
- **Member** (adherent) : peut emprunter des livres
- **Loan** (pret) : associe un livre et un adherent, avec statut `ACTIVE` ou `RETURNED`

Regles metier principales :
- un livre deja emprunte (pret `ACTIVE`) ne peut pas etre re-emprunte
- la duree de pret par defaut est de 14 jours
- un pret deja cloture ne peut pas etre rendu une seconde fois
- l'email d'un adherent doit etre unique

## Architecture

```
controller   -> endpoints REST, validation des entrees
service      -> logique metier, transactions
repository   -> acces aux donnees (Spring Data JPA)
domain       -> entites JPA
dto          -> objets d'echange (records) avec validation Bean Validation
mapper       -> conversion entite <-> DTO
exception    -> exceptions metier + gestion centralisee (@RestControllerAdvice)
```

## Environnements et configuration

| Profil | Fichier                     | Base de donnees                  | Flyway                                |
|--------|-----------------------------|-----------------------------------|----------------------------------------|
| dev    | `application-dev.yml`       | PostgreSQL local (docker-compose) | `db/migration` + `db/dev-data` (jeu de donnees) |
| prod   | `application-prod.yml`      | PostgreSQL (variables d'env.)     | `db/migration` uniquement             |
| test   | `src/test/resources/application.yml` | H2 en memoire          | desactive (schema genere par Hibernate) |

Le profil actif est controle par la variable d'environnement `SPRING_PROFILES_ACTIVE` (par defaut `dev`).

Variables d'environnement utilisees en prod : `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`.

## Demarrage en local

1. Demarrer la base PostgreSQL de developpement :
   ```bash
   docker compose up -d
   ```
2. Lancer l'application (profil `dev` par defaut) :
   ```bash
   ./mvnw spring-boot:run
   ```
3. Documentation API disponible sur : http://localhost:8080/swagger-ui.html

## Tests

```bash
./mvnw test
```

Les tests couvrent :
- tests unitaires de service avec Mockito (logique metier, regles d'emprunt/retour)
- tests de repository avec `@DataJpaTest` (H2)
- tests de controleur avec `@WebMvcTest` et MockMvc (validation, codes HTTP, gestion d'erreurs)
- test de chargement du contexte Spring (`@SpringBootTest`)

## Endpoints principaux

| Methode | URL                          | Description                       |
|---------|------------------------------|------------------------------------|
| GET     | `/api/authors`               | Liste des auteurs                  |
| POST    | `/api/authors`                | Creation d'un auteur               |
| GET     | `/api/books?authorId=`       | Liste des livres (filtre optionnel)|
| POST    | `/api/books`                  | Creation d'un livre                |
| GET     | `/api/members`                | Liste des adherents                |
| POST    | `/api/loans`                   | Creation d'un pret                 |
| POST    | `/api/loans/{id}/return`       | Cloture d'un pret (retour du livre)|
