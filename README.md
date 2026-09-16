# ApexGrid Engine

A lightweight, production-grade esports tournament management and bracket orchestration engine built with Java 17 and Spring Boot 3.

ApexGrid automates single-elimination tournament logistics from team roster registration to championship resolution. It features dynamic bracket generation and automated winner progression: once match scores are reported, winners advance automatically to the next round fixture until a champion is determined.

---

## Architectural Principles

* **Strict Layer Separation:** `Controller -> DTO -> Service -> Repository -> Entity`
* **Zero Entity Leakage:** Entities never touch the API surface. Communication relies entirely on dedicated Request and Response DTOs.
* **Direct Payloads:** Controllers return domain DTOs directly using `@ResponseStatus` instead of verbose `ResponseEntity` wrappers.
* **Database Isolation:** Enforces cascading operations, foreign key integrity, and transactional consistency in MySQL.

---

## Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.x (Spring Web, Spring Data JPA)
* **Database:** MySQL 8.x
* **Build Tool:** Maven Wrapper (`mvnw`)
* **Utilities:** Project Lombok, Jakarta Bean Validation

---

## Project Structure

```text
com.apexgrid.engine
├── controller       # REST endpoints (Tournament, Team, Bracket)
├── dto
│   ├── request      # Validated incoming payloads
│   └── response     # Outgoing response contracts
├── entity           # JPA domain models (Tournament, Team, Player, MatchFixture)
├── enums            # GameType, TournamentStatus, MatchStatus
├── exception        # Global exception handler & custom exceptions
├── repository       # Spring Data JPA repositories
└── service          # Bracket algorithm & tournament progression logic

