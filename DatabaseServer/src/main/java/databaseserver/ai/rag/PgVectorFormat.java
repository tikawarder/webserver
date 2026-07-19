package databaseserver.ai.rag;

import dev.langchain4j.data.embedding.Embedding;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * pgvector accepts vector literals as text in the form "[0.1,0.2,0.3]" —
 * this turns a langchain4j Embedding (float[] under the hood) into exactly that string.
 */
public class PgVectorFormat {

    public static String toLiteral(Embedding embedding) {
        float[] values = embedding.vector();
        String joined = IntStream.range(0, values.length)
                .mapToObj(i -> Float.toString(values[i]))
                .collect(Collectors.joining(","));
        return "[" + joined + "]";
    }
}
