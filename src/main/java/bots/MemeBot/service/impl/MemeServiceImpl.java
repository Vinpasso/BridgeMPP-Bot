package bots.MemeBot.service.impl;

import bots.MemeBot.dto.Meme;
import bots.MemeBot.dto.Response;
import bots.MemeBot.service.MemeService;
import bots.NewsBot.dto.Articles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Singleton
public class MemeServiceImpl implements MemeService {
    @Inject ObjectMapper objectMapper;

    private List<Meme> memes;

    @Override
    public List<Meme> getMemes() {
        List<Meme> memes = fetchMemes();
        return memes;
    }

    @Override
    public Meme getMeme(int id) {
        for (Meme meme : fetchMemes()) {
            if (meme.getId() == id) {
                return meme;
            }
        }
        return null;
    }

    @Override
    public Meme getMeme(String message) {
        for (Meme meme : fetchMemes()) {
            if (message.toLowerCase().contains(meme.getName().toLowerCase())) {
                return meme;
            }
        }
        return null;
    }

    private List<Meme> fetchMemes() {
        if (memes != null) {
            return memes;
        }
        URL url = null;
        try {
            url = new URL("hhttps://api.imgflip.com/get_memes");
            URLConnection con = url.openConnection();
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            Object o = con.getContent();
            Response res = objectMapper.readValue(url, Response.class);
            if (!res.isSuccess()) {
                throw new RuntimeException("Error fetching memes");
            }
            return res.getMemes();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching memes");
        }

    }

}
