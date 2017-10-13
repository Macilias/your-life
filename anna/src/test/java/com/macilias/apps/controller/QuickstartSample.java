package com.macilias.apps.controller;

import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;

import java.util.Arrays;

public class QuickstartSample {
    public static void main(String... args) throws Exception {

        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = "Hello, world!";
            Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16).build();
            AnalyzeSyntaxResponse response = language.analyzeSyntax(request);

            System.out.printf("Text: %s%n", text);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());

            System.out.println("___________________________________________________");
            System.out.println(response);
            System.out.println("___________________________________________________");
        }
    }
}
