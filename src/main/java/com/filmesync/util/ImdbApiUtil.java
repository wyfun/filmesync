package com.filmesync.util;

import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

public class ImdbApiUtil {

    private static final String IMDB_API_URL = "https://v3.sg.media-imdb.com/suggestion/a/{filmName}.json";
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches a movie poster URL from IMDb API based on the film name
     * @param filePath The path of the film file
     * @return The poster URL or null if not found
     */
    public static String getPosterUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "https://via.placeholder.com/200x300.png?text=Sem+Imagem";
        }

        try {
            // Extract film name from path
            int lastSlashIndex = filePath.lastIndexOf('/');
            if (lastSlashIndex < 0 || lastSlashIndex >= filePath.length() - 1) {
                return "https://via.placeholder.com/200x300.png?text=Nome+Inválido";
            }

            String fileName = filePath.substring(lastSlashIndex + 1);
            if (!fileName.toLowerCase().endsWith(".mp4")) {
                return "https://via.placeholder.com/200x300.png?text=Não+é+MP4";
            }

            String filmName = fileName.replace(".mp4", "");
            if (filmName.isEmpty()) {
                return "https://via.placeholder.com/200x300.png?text=Nome+Vazio";
            }

            // Clean up the film name for the API
            filmName = cleanFilmName(filmName);

            // Encode the film name for the URL
            String encodedFilmName = URLEncoder.encode(filmName, StandardCharsets.UTF_8.toString());

            // Create the API URL
            String apiUrl = IMDB_API_URL.replace("{filmName}", encodedFilmName);

            System.out.println("Fetching poster for: " + filmName + " from URL: " + apiUrl);

            try {
                // Make the API request with timeout
                RestTemplate template = new RestTemplate();
                template.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory());
                ((org.springframework.http.client.SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(5000);
                ((org.springframework.http.client.SimpleClientHttpRequestFactory) template.getRequestFactory()).setReadTimeout(5000);

                Map<String, Object> response = template.getForObject(apiUrl, Map.class);

                if (response != null && response.containsKey("d") && response.get("d") instanceof java.util.List) {
                    java.util.List<Map<String, Object>> results = (java.util.List<Map<String, Object>>) response.get("d");

                    if (!results.isEmpty()) {
                        Map<String, Object> firstResult = results.get(0);

                        if (firstResult.containsKey("i") && firstResult.get("i") instanceof Map) {
                            Map<String, Object> imageInfo = (Map<String, Object>) firstResult.get("i");

                            if (imageInfo.containsKey("imageUrl")) {
                                String posterUrl = (String) imageInfo.get("imageUrl");
                                System.out.println("Found poster URL: " + posterUrl);
                                return posterUrl;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("API request error for " + filmName + ": " + e.getMessage());
                // Continue with placeholder
            }
        } catch (Exception e) {
            System.err.println("Error processing file path " + filePath + ": " + e.getMessage());
        }

        // Return a placeholder if no poster is found
        return "https://via.placeholder.com/200x300.png?text=Sem+Imagem";
    }

    /**
     * Cleans up the film name for better API results
     * @param filmName The raw film name
     * @return The cleaned film name
     */
    private static String cleanFilmName(String filmName) {
        // Replace underscores and dots with spaces
        filmName = filmName.replace("_", " ").replace(".", " ");

        // Remove year in parentheses if present (e.g., "Movie Name (2020)")
        filmName = filmName.replaceAll("\\s*\\(\\d{4}\\)\\s*", " ");

        // Remove resolution info (e.g., "1080p", "720p")
        filmName = filmName.replaceAll("\\s*\\d{3,4}p\\s*", " ");

        // Remove common file descriptors
        String[] descriptorsToRemove = {"HDTV", "BluRay", "WEB-DL", "WEBRip", "DVDRip", "x264", "x265", "HEVC"};
        for (String descriptor : descriptorsToRemove) {
            filmName = filmName.replaceAll("(?i)\\s*" + Pattern.quote(descriptor) + "\\s*", " ");
        }

        // Trim and remove multiple spaces
        return filmName.trim().replaceAll("\\s+", " ");
    }
}
