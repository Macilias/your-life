package com.macilias.apps.controller;

import com.google.cloud.language.v1.*;

public class SyntaxDetector {

    public static AnalyzeSyntaxResponse getSyntax(String text) {

        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16).build();
            return language.analyzeSyntax(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
