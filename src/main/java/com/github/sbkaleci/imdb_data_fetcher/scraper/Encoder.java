package com.github.sbkaleci.imdb_data_fetcher.scraper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Encoder {
    public static String UTF8Encoder(String input) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }
}
