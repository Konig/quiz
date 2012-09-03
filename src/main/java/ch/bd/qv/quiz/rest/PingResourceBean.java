/*
 * Copyright 2012 thierry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.bd.qv.quiz.rest;

import ch.bd.qv.quiz.ping.IPingResponder;
import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 *
 * @author thierry
 */
@Stateless
@Path("ping")
public class PingResourceBean {

    private static final Logger LOGGER = Logger.getLogger(PingResourceBean.class);
    private static final String FORMATTER = "[%3s] %-30s %s%n";
    private static final String FORMATTER_APPENDIX = "%37s%s%n";
    @Inject
    @Any
    private Instance<IPingResponder> responders;
    @Context
    private HttpServletRequest request;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        LOGGER.debug("ping called... from: "+request.getRemoteAddr());
        StringBuilder sb = new StringBuilder();
        sb.append("Application is: [{0}]\n");
        sb.append("==============================================\n");
        Formatter format = new Formatter(sb);
        boolean anyFailures = false; 
        for (IPingResponder responder : Lists.newArrayList(responders.iterator())) {
            String simpleName = responder.getClass().getSimpleName();
            try {
                if(!responder.isValid())
                {
                    anyFailures = true; 
                }
                format.format(FORMATTER, responder.isValid() ? "OK" : "NOK", simpleName, responder.getMessage().get(0));
                if (responder.getMessage().size() > 1) {
                    List<String> messages = responder.getMessage();
                    for (int i = 1; i < messages.size(); i++) {
                        format.format(FORMATTER_APPENDIX, ' ', messages.get(i));
                    }
                }
            } catch (Exception e) {
                LOGGER.fatal("responder: " + simpleName + " crashed", e);
                format.format(FORMATTER, "NOK", simpleName, " is crashed. check log for details!");
            }
        }

        return MessageFormat.format(sb.toString(), anyFailures ? "NOK" : "OK");
    }
}
