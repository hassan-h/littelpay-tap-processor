# Littlepay Coding Exercise – Transit Trip Processing

This project simulates fare calculation for a public transport system using tap-on/tap-off data. It reads a CSV of tap events, calculates fare and trip status, and writes trip summaries into another CSV file.

---

## 🧩 Features

- Reads tap events from CSV
- Groups taps into `COMPLETED`, `INCOMPLETE`, or `CANCELLED` trips
- Calculates fare using configurable rules from `application.yml`
- Outputs trips to a CSV file
- Robust error handling and input validation
- Unit + Integration tests using JUnit 5
- Extensible, modular, and clean Spring Boot structure

---

## 🔧 Build the Project

mvn clean package

## ▶️ Run the Application

Option 1: Use Default Input/Output (Configured in application.yml)

mvn spring-boot:run

OR

java -jar target\tap-processor-1.0.0.jar

Option 2: Provide Custom File Paths

java -jar target/tap-processor-1.0.0.jar path/to/taps.csv path/to/trips.csv

OR

mvn spring-boot:run -Dspring-boot.run.arguments="path/to/taps.csv path/to/trips.csv"

## 🧪 Run Tests

mvn test

Unit Tests:
	
	FareCalculatorTest
	TripServiceTest
Integration Test:
	
	IntegrationTest (full CSV → processing → CSV)
	
## 🧱 Tech Stack
Java 17
Spring Boot
OpenCSV
Lombok
JUnit 5

## 👨‍💻 Author
Hassan Hanif