package bots.MemeBot.service;

import bots.MemeBot.dto.Meme;

import java.util.List;

public interface MemeService {
    List<Meme> getMemes();
    Meme getMeme(int id);
    Meme getMeme(String message);
}
