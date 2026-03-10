import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;

record WeatherRecord(LocalDate date, double temperature, int humidity, double precipitation) {}

public interface WeatherAnalyzer {

    static void main(String[] args) {
        String input = JOptionPane.showInputDialog(null, "Enter a month number (1-12) to analyze:", "Weather Analyzer", JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.isBlank()) return;

        int targetMonth = Integer.parseInt(input.trim());
        Path csvPath = Path.of("weatherdata.csv");

        try {
            List<WeatherRecord> records = parseCsv(csvPath);

            double avgTemp = averageTemperatureForMonth(records, targetMonth);
            long hotDays = daysAboveThreshold(records, 30.0);
            long rainyDays = countRainyDays(records);
            String category = determineWeatherCategory(avgTemp);

            records.forEach(WeatherAnalyzer::printEventDetails);

            generateAndOpenGuiReport(targetMonth, avgTemp, hotDays, rainyDays, category);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading weatherdata.csv!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static List<WeatherRecord> parseCsv(Path path) throws IOException {
        return Files.lines(path)
                .skip(1)
                .map(line -> line.split(","))
                .map(data -> new WeatherRecord(
                        LocalDate.parse(data[0]),
                        Double.parseDouble(data[1]),
                        Integer.parseInt(data[2]),
                        Double.parseDouble(data[3])
                ))
                .toList();
    }

    static double averageTemperatureForMonth(List<WeatherRecord> records, int month) {
        return records.stream()
                .filter(r -> r.date().getMonthValue() == month)
                .mapToDouble(WeatherRecord::temperature)
                .average()
                .orElse(Double.NaN);
    }

    static long daysAboveThreshold(List<WeatherRecord> records, double threshold) {
        return records.stream()
                .filter(r -> r.temperature() > threshold)
                .count();
    }

    static long countRainyDays(List<WeatherRecord> records) {
        return records.stream()
                .filter(r -> r.precipitation() > 0.0)
                .count();
    }

    static String determineWeatherCategory(double temp) {
        if (Double.isNaN(temp)) return "No Data";

        int categoryKey = (int) temp / 10;

        return switch (categoryKey) {
            case 3, 4, 5 -> "Hot ☀️";
            case 2 -> "Warm 🌤️";
            case 1 -> "Cool 🌥️";
            default -> "Cold ❄️";
        };
    }

    static void printEventDetails(Object obj) {
        switch (obj) {
            case WeatherRecord(LocalDate d, double t, int h, double p) when p > 10.0 ->
                    System.out.println("Heavy Rain Alert on " + d + "!");
            case WeatherRecord r ->
                    System.out.println("Standard weather on " + r.date());
            default ->
                    System.out.println("Unknown data type.");
        }
    }

    /// GUI
    static void generateAndOpenGuiReport(int month, double avgTemp, long hotDays, long rainyDays, String category) throws IOException {
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Weather Report</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f3f4f6; color: #1f2937; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
                    .card { background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); max-width: 400px; width: 100%%; }
                    h1 { font-size: 1.5rem; border-bottom: 2px solid #e5e7eb; padding-bottom: 0.5rem; margin-bottom: 1.5rem; }
                    .stat { display: flex; justify-content: space-between; padding: 0.5rem 0; font-size: 1.1rem; }
                    .stat span:last-child { font-weight: bold; color: #3b82f6; }
                    .category { margin-top: 1.5rem; text-align: center; font-size: 1.5rem; font-weight: bold; color: #ef4444; background: #fee2e2; padding: 0.75rem; border-radius: 8px; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h1>Weather Analysis - Month %d</h1>
                    <div class="stat"><span>Average Temp:</span> <span>%.1f °C</span></div>
                    <div class="stat"><span>Days > 30°C:</span> <span>%d</span></div>
                    <div class="stat"><span>Rainy Days:</span> <span>%d</span></div>
                    <div class="category">%s</div>
                </div>
            </body>
            </html>
            """.stripIndent().formatted(month, avgTemp, hotDays, rainyDays, category);

        File htmlFile = new File("WeatherReport.html");
        Files.writeString(htmlFile.toPath(), html);

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(htmlFile.toURI());
        }
    }
}
