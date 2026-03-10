# Modern Java Weather Data Analyzer

A high-performance, strictly functional-style Java application that analyzes weather data from a CSV file. Built entirely without explicit `class` declarations, this project relies on interfaces, records, and functional pipelines.

## Features
- Zero Explicit Classes: The entire architecture uses an `interface` acting as the application container and a `record` for immutable data representation.
* **GUI:** Uses Swing's `JOptionPane` for lightweight input collection and dynamically generates an HTML+CSS styling dashboard that opens automatically in the default web browser.
* **Functional Data Processing:** Fully leverages lambdas and streams to calculate monthly temperature averages, identify hot days, and count precipitation events.

## Setup & Execution

1. Ensure you have **JDK 23** installed (or JDK 21+ with preview features).
2. Place `weatherdata.csv`, `WeatherAnalyzer.java`, and `README.md` in the same directory.
3. Compile the application:
   ```bash
   javac WeatherAnalyzer.java
