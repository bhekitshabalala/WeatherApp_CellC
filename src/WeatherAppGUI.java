import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

public class WeatherAppGUI extends JFrame {

    private JComboBox<String> cityCombo;
    private JTextField customCityField;
    private JButton searchButton;
    private JButton clearButton;
    private JPanel resultPanel;

    private static final String API_KEY = "9e5d64172f01b56e57b6ac9090fbef48";

    public WeatherAppGUI() {
        setTitle("Cell C Weather App");
        setSize(550, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Color cellCOrange = Color.decode("#FF6600");
        Color darkGray = Color.decode("#222222");

        // Logo
        ImageIcon logoIcon = new ImageIcon("C:/xampp/htdocs/WeatherApp_CellC/src/logo/cellc_logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setBounds(20, 10, 100, 100);
        add(logoLabel);

        // Heading
        JLabel heading = new JLabel("Weather App - Powered by Cell C");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setBounds(130, 40, 380, 30);
        heading.setForeground(cellCOrange);
        add(heading);

        // Labels
        JLabel cityLabel = new JLabel("🌍 Select City:");
        cityLabel.setBounds(30, 130, 100, 25);
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cityLabel.setForeground(darkGray); // ← updated to dark gray
        add(cityLabel);

        String[] cities = {"Johannesburg", "Durban", "Polokwane", "Pretoria", "Bloemfontein", "Cape Town"};
        cityCombo = new JComboBox<>(cities);
        cityCombo.setBounds(130, 130, 220, 30);
        cityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cityCombo.setBackground(Color.WHITE);
        cityCombo.setForeground(darkGray);
        cityCombo.setBorder(new RoundedBorder(10));
        add(cityCombo);

        JLabel orLabel = new JLabel("Or type a city:");
        orLabel.setBounds(30, 170, 100, 25);
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orLabel.setForeground(darkGray); // ← updated to dark gray
        add(orLabel);

        customCityField = new JTextField();
        customCityField.setBounds(130, 170, 220, 30);
        customCityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customCityField.setBorder(new RoundedBorder(10));
        add(customCityField);

        // Buttons
        searchButton = new JButton("Get Weather");
        searchButton.setBounds(130, 210, 140, 35);
        searchButton.setBackground(cellCOrange);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setBorder(new RoundedBorder(15));
        add(searchButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(280, 210, 70, 35);
        clearButton.setBackground(Color.DARK_GRAY);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearButton.setBorder(new RoundedBorder(15));
        add(clearButton);

        // Result display
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBounds(30, 270, 480, 180);
        resultPanel.setBackground(Color.WHITE);
        add(resultPanel);

        // Actions
        searchButton.addActionListener(e -> {
            String customCity = customCityField.getText().trim();
            String selectedCity = (String) cityCombo.getSelectedItem();

            if (!customCity.isEmpty()) {
                fetchWeather(customCity);
            } else if (selectedCity != null && !selectedCity.isEmpty()) {
                fetchWeather(selectedCity);
            } else {
                showMessage("⚠️ Please select or enter a city.");
            }
        });

        clearButton.addActionListener(e -> {
            cityCombo.setSelectedIndex(0);
            customCityField.setText("");
            resultPanel.removeAll();
            resultPanel.revalidate();
            resultPanel.repaint();
        });
    }

    private void fetchWeather(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + encodedCity + "&appid=" + API_KEY + "&units=metric";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JSONObject obj = new JSONObject(json.toString());

            String weatherDesc = obj.getJSONArray("weather").getJSONObject(0).getString("description");
            double temp = obj.getJSONObject("main").getDouble("temp");
            int humidity = obj.getJSONObject("main").getInt("humidity");

            resultPanel.removeAll();

            JLabel cityTitle = new JLabel("📍 Weather in " + capitalize(city));
            cityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            cityTitle.setForeground(Color.decode("#FF6600"));
            cityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            resultPanel.add(cityTitle);

            resultPanel.add(createIconRow("C:/xampp/htdocs/WeatherApp_CellC/src/icons/weather.png", "Weather: " + capitalize(weatherDesc)));
            resultPanel.add(createIconRow("C:/xampp/htdocs/WeatherApp_CellC/src/icons/temperature.png", String.format("Temperature: %.2f °C", temp)));
            resultPanel.add(createIconRow("C:/xampp/htdocs/WeatherApp_CellC/src/icons/humidity.png", "Humidity: " + humidity + "%"));

            resultPanel.revalidate();
            resultPanel.repaint();

        } catch (Exception e) {
            showMessage("⚠️ Could not fetch weather.\nCheck city name or internet.");
            e.printStackTrace();
        }
    }

    private JPanel createIconRow(String iconPath, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon(iconPath);
        Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaled));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(Color.decode("#222222"));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        row.add(iconLabel);
        row.add(textLabel);

        return row;
    }

    private void showMessage(String message) {
        resultPanel.removeAll();
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgLabel.setForeground(Color.RED);
        resultPanel.add(msgLabel);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppGUI().setVisible(true));
    }

    // Custom rounded border
    class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 4;
            return insets;
        }
    }
}
