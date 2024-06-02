package com.github.sbkaleci.imdb_data_fetcher;

import com.github.sbkaleci.imdb_data_fetcher.scraper.Encoder;
import com.github.sbkaleci.imdb_data_fetcher.scraper.Scraper;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class Main {
    private static CardLayout cardLayout = new CardLayout();
    private static JPanel cardPanel = new JPanel(cardLayout);
    private static JFrame frame = new JFrame("IMDb Data Fetcher");
    private static DefaultListModel<MovieItem> movieListModel = new DefaultListModel<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        cardPanel.setLayout(cardLayout);
        cardPanel.add(buildSearchView(), "SearchView");
        frame.add(cardPanel);

        frame.setVisible(true);
    }

    private static JPanel buildSearchView() {
        JPanel searchView = new JPanel(new BorderLayout(10, 10));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));

        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel topPanel = new JPanel();
        topPanel.add(searchField);
        topPanel.add(searchButton);
        searchView.add(topPanel, BorderLayout.NORTH);

        JList<MovieItem> resultList = new JList<>(movieListModel);
        resultList.setCellRenderer(new MovieCellRenderer());
        searchView.add(new JScrollPane(resultList), BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String input = searchField.getText();
            String encodedInput = Encoder.UTF8Encoder(input);
            List<String> urls = Scraper.getMovieUrls(encodedInput);
            movieListModel.removeAllElements();
            for (String url : urls) {
                List<String> details = Scraper.getMovieData(url);
                if (details != null && !details.isEmpty()) {
                    // Assuming that the details list follows the same order as mentioned in getMovieData()
                    movieListModel.addElement(new MovieItem(details.get(0), details.get(6), url));
                }
            }
        });

        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                MovieItem selectedMovie = resultList.getSelectedValue();
                List<String> details = Scraper.getMovieData(selectedMovie.getUrl());
                if (details != null) {
                    cardPanel.add(buildDetailView(details), "DetailView");
                    cardLayout.show(cardPanel, "DetailView");
                }
            }
        });

        return searchView;
    }

    private static JPanel buildDetailView(List<String> details) {
        JPanel detailView = new JPanel(new BorderLayout());
        JTextArea detailTextArea = new JTextArea();
        detailTextArea.setEditable(false);
        detailTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        detailTextArea.setWrapStyleWord(true);
        detailTextArea.setLineWrap(true);
    
        // Set text area content with details provided
        if (details != null && !details.isEmpty()) {
            StringBuilder detailBuilder = new StringBuilder();
            detailBuilder.append("Title: ").append(details.get(0)).append("\n");
            detailBuilder.append("Year: ").append(details.get(1)).append("\n");
            detailBuilder.append("Description: ").append(details.get(2)).append("\n");
            detailBuilder.append("IMDb Score: ").append(details.get(3)).append("\n");
            detailBuilder.append("Director: ").append(details.get(4)).append("\n");
            detailBuilder.append("Cast: ").append(details.get(5)).append("\n");
            detailTextArea.setText(detailBuilder.toString());
        }
    
        JScrollPane scrollPane = new JScrollPane(detailTextArea);
        detailView.add(scrollPane, BorderLayout.CENTER);
    
        // Load and display the movie poster if URL is available
        if (details.size() > 6 && !details.get(6).isEmpty()) {
            try {
                URL imageUrl = new URL(details.get(6));
                ImageIcon icon = new ImageIcon(imageUrl);
                Image image = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                detailView.add(imageLabel, BorderLayout.EAST);
            } catch (Exception e) {
                detailView.add(new JLabel("Image not available"), BorderLayout.EAST);
            }
        }
    
        // Back button to return to the search view
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "SearchView"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        detailView.add(buttonPanel, BorderLayout.SOUTH);
    
        return detailView;
    }
    

    static class MovieItem {
        private String title;
        private String imageUrl;
        private String url;

        public MovieItem(String title, String imageUrl, String url) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getUrl() {
            return url;
        }
    }

    static class MovieCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MovieItem) {
                MovieItem movieItem = (MovieItem) value;
                setText(movieItem.getTitle());
                setIcon(loadImageIcon(movieItem.getImageUrl()));
            }
            return component;
        }

        private ImageIcon loadImageIcon(String imageUrl) {
            try {
                URL url = new URL(imageUrl);
                ImageIcon icon = new ImageIcon(url);
                Image image = icon.getImage().getScaledInstance(50, 75, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            } catch (Exception e) {
                // Handle exception and return a placeholder or empty icon if needed
                return new ImageIcon();
            }
        }
    }
}
