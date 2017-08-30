package com.macilias.apps.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * your-life
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class APIUtil {

    public static final String INTENT = "Intent";

    public static void printResponseAsJson(ServletResponse servletResponse, Object response) throws IOException {
        Gson gson = new GsonBuilder().create();
        servletResponse.setContentType("application/json");
        servletResponse.setCharacterEncoding("UTF-8");
        String responseAsJSON = gson.toJson(response);
        PrintWriter writer = servletResponse.getWriter();
        writer.print(responseAsJSON);
    }


}
