package com.github.sbkaleci.imdb_data_fetcher.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {
    public static List<String> getMovieUrls(String query) {
        List<String> urls = new ArrayList<>();
        System.out.println(query);
        try {
            Document doc = Jsoup.connect("https://www.imdb.com/find/?q=" + query).get();
            Elements elements = doc.select("[class=ipc-metadata-list-summary-item__t]");

            for (Element element : elements) {
                urls.add("https://www.imdb.com" + element.attr("href"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    public static String biggestPoster(Elements elements) {
        if (elements.isEmpty()) {
            return "";
        }
        Element firstElement = elements.first();
        String srcsetAttribute = firstElement.attr("srcset");
        String pattern = "(https://m\\.media-amazon\\.com/images/.*?\\.jpg)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(srcsetAttribute);
        String largestImageUrl = "";
        int maxWidth = 0;
        while (m.find()) {
            String imageUrl = m.group(1);
            String[] parts = imageUrl.split("_");
            if (parts.length >= 2) {
                try {
                    int width = Integer.parseInt(parts[parts.length - 2].replaceAll("[^\\d]", ""));
                    if (width > maxWidth) {
                        maxWidth = width;
                        largestImageUrl = imageUrl;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        return largestImageUrl;
    }

    public static List<String> getMovieData(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            Elements titleElement = doc.select("[class=hero__primary-text]");
            String title = titleElement.text();

            Elements yearElement = doc
                    .select("[class=ipc-inline-list ipc-inline-list--show-dividers sc-d8941411-2 cdJsTz baseAlt]");
            String year = yearElement.text();

            Elements descriptionElement = doc.select("[class=sc-466bb6c-0 hlbAws]");
            String description = descriptionElement.text();

            Elements imdbScoreElement = doc.select("[class=sc-bde20123-1 cMEQkK]");
            String imdbScore = imdbScoreElement.get(0).text();

            Elements directorElement = doc.select(
                    "[class=ipc-metadata-list-item__list-content-item ipc-metadata-list-item__list-content-item--link]");
            String director = directorElement.get(0).text();

            Elements castElement = doc.select("[class=sc-bfec09a1-1 gCQkeh]");
            String cast = castElement.text();

            Elements posterElement = doc.select("[class=ipc-image]");
            String poster = biggestPoster(posterElement);

            List<String> MovieDetails = new ArrayList<>();
            Collections.addAll(MovieDetails, title, year, description, imdbScore, director, cast, poster);

            System.out.println("Title: " + title);
            System.out.println("Year/Rating/Run Time: " + year);
            System.out.println("Description: " + description);
            System.out.println("IMDb Score: : " + imdbScore);
            System.out.println("Director: " + director);
            System.out.println("Cast: " + cast);
            System.out.println("Poster: " + poster);

            return MovieDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
