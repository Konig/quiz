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

import ch.bd.qv.quiz.config.ConfigKeys;
import ch.bd.qv.quiz.ejb.ConfigBean;
import ch.bd.qv.quiz.entities.Config;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.List;
import javax.inject.Inject;

/**
 * checks the database and reads the log4j key
 * @author thierry
 */
public class DBPingResponder implements IPingResponder {

    private static final String TEMPLATE = "Configuration key for log4j was: {0}";
    @Inject
    private ConfigBean configBean;
    private String result;

    @Override
    public boolean isValid() {
        Config config = configBean.getConfigForKey(ConfigKeys.LOG4J_PROPERTIES);
        if (config == null) {
            this.result = "CONFIG WAS NULL!";
            return false;
        } else {
            result = config.getConfigValue();
        }
        return true;

    }

    @Override
    public List<String> getMessage() {
        return Lists.newArrayList(MessageFormat.format(TEMPLATE, result));

    }
}
