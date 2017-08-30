package com.macilias.apps.view;

import com.macilias.apps.controller.Anna;
import com.macilias.apps.controller.AnnaImpl;
import com.macilias.apps.model.MarvinQuotes;
import com.macilias.apps.model.Sentence;

import java.util.Optional;
import java.util.Scanner;

/**
 * Run this class to consult Anna
 */
public class Main {


    public static void main(String... args) {

        Anna anna = new AnnaImpl();

        Scanner in = new Scanner(System.in);

        System.out.println("Hi, I´m Anna, I know I won't like it but proceed:");

        while (in.hasNextLine()) {
            Optional<String> optionalAnswer = anna.consume(new Sentence(in.nextLine()));
            if (optionalAnswer.isPresent()) {
                String answer = optionalAnswer.get();
                System.out.println(answer);
                if (MarvinQuotes.END.contains(answer)) {
                    System.exit(0);
                }
            }
        }

    }
}
