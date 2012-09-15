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
package ch.bd.qv.quiz.ping;

import ch.bd.qv.quiz.base.ApplicationStartupBean;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.List;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 * checks the configuration for consistency 
 * type: ManagedBean
 *
 * @author thierry
 */
public class ConfigurationPingResponder implements IPingResponder {

    private static final String TEMPLATE = "Configuration invalid: {0}";
    private static final String ALL_RIGHT_TEMPLATE = "Configuration is fine";
    private static final Logger LOGGER = Logger.getLogger(ConfigurationPingResponder.class);
    @Inject
    private ApplicationStartupBean startupBean;
    private String result;

    @Override
    public boolean isValid() {
        try {
            startupBean.verify();
            result = ALL_RIGHT_TEMPLATE;
            return true;
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("configuration incomplete!", iae);
            result = MessageFormat.format(TEMPLATE, iae.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.fatal("unforseen exception caught,", e);
            result = MessageFormat.format(TEMPLATE, e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getMessage() {
        return Lists.newArrayList(result);

    }
}
