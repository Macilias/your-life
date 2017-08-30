package com.macilias.apps.controller;

import com.macilias.apps.model.Sentence;
import com.macilias.apps.model.anna.api.v1.AnnaRequest;
import com.macilias.apps.model.anna.api.v1.AnnaResponse;
import com.macilias.apps.model.api.v1.CustomRequest;
import com.macilias.apps.model.api.v1.CustomResponse;
import com.macilias.apps.model.sidekick.api.v1.Argument;
import com.macilias.apps.model.sidekick.api.v1.ArgumentName;
import com.macilias.apps.model.api.v1.History;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * AnnaAPI_v1
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class AnnaAPI_v1 implements Filter {

    private static final Logger LOG = Logger.getLogger(AnnaAPI_v1.class);

    private Anna anna;
    private History history;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("init() " + filterConfig);
        anna = WebApplicationContextUtils.
                getRequiredWebApplicationContext(filterConfig.getServletContext()).
                getBean(AnnaImpl.class);
        history = WebApplicationContextUtils.
                getRequiredWebApplicationContext(filterConfig.getServletContext()).
                getBean(History.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CustomRequest request = parseRequest(servletRequest);
        CustomResponse response = createResponse(request);
        request.setResponse(response);
        history.addRequest(request);

        LOG.info("request  as String: " + request);
        LOG.info("response as String: " + response);
        APIUtil.printResponseAsJson(servletResponse, response);
    }

    private CustomResponse createResponse(CustomRequest request) {
        Optional<Argument> optionalArgument = request.getOptionalArgument(ArgumentName.WHAT);
        if (!optionalArgument.isPresent()) {
            return new AnnaResponse("Sorry, I was not able to understand your intention.");
        }
        Argument what = optionalArgument.get();
        Optional<String> answer = anna.consume(new Sentence(what.getValuesAsString()));
        return answer.map(AnnaResponse::new).orElseGet(() -> new AnnaResponse(""));
    }

    private CustomRequest parseRequest(ServletRequest servletRequest) {
        CustomRequest customRequest = new AnnaRequest(AnnaAPI_v1.class.getSimpleName());
        Map m = servletRequest.getParameterMap();
        Set s = m.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {

            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) it.next();

            String key = entry.getKey().toLowerCase();
            String[] values = entry.getValue();

            LOG.info("key: " + key + " values.length: " + values.length + " first value is: " + values[0]);
            customRequest.setIntent("text");

            if (key.equalsIgnoreCase("text") || key.equalsIgnoreCase("sentence") || key.equalsIgnoreCase("q")) {
                customRequest.addArgument(ArgumentName.WHAT, values);
            } else {
                LOG.warn("Sorry, I don't know what to do with it [" + key + "], its not a AnnaIntent and not a Argument");
            }

        }
        return customRequest;
    }

    @Override
    public void destroy() {
        LOG.debug("destroy()");
    }
}
