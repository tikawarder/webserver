package databaseserver.ai.rag;

import java.util.ArrayList;
import java.util.List;

/*
 * Splits raw text into overlapping word-count chunks.
 * Overlap exists so a fact split across a chunk boundary still appears
 * whole in at least one chunk — without it, the sentence right at the
 * cut point could lose context in both neighboring chunks.
 */
public class TextChunker {

    public static List<String> chunk(String text, int chunkSizeWords, int overlapWords) {
        String[] words = text.trim().split("\\s+");
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < words.length) {
            int end = Math.min(start + chunkSizeWords, words.length);
            chunks.add(String.join(" ", java.util.Arrays.copyOfRange(words, start, end)));
            if (end == words.length) {
                break;
            }
            start = end - overlapWords;
        }
        return chunks;
    }
}
