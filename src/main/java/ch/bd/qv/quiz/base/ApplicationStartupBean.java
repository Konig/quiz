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
package ch.bd.qv.quiz.base;

import ch.bd.qv.quiz.config.ConfigKeys;
import ch.bd.qv.quiz.ejb.ConfigBean;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author thierry
 */
@Singleton
@Startup
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ApplicationStartupBean {

    @Inject
    private ConfigBean configBean;
    private static Logger LOGGER;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void verify() {
        LOGGER.info("verifying application configuration data.. ");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(configBean.getConfigForKey(
                ConfigKeys.SUPPORTED_LANGUAGES).getConfigValue()), "SupportedLanguages not defined! Hint: e.g. 'de,fr,it'");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(configBean.getConfigForKey(
                ConfigKeys.RESOURCES_FOLDER).getConfigValue()), "Resourcesfolder not defined! Hint : Path/To/folder");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(configBean.getConfigForKey(
                ConfigKeys.NO_OF_CHECK_QUESTION).getConfigValue()), "checkquestions not defined! Hint: positive Integer");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(configBean.getConfigForKey(
                ConfigKeys.NO_OF_FREE_QUESTION).getConfigValue()), "freequestions not defined! Hint: positive Integer");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(configBean.getConfigForKey(
                ConfigKeys.NO_OF_RADIO_QUESTION).getConfigValue()), "radioquestions not defined! Hint: positive Integer");
        LOGGER.info("verify passed ");
    }

    @PostConstruct
    public void postConstruct() {

        try {
            PropertyConfigurator.configureAndWatch(configBean.getConfigForKey(ConfigKeys.LOG4J_PROPERTIES).getConfigValue());
            LOGGER = Logger.getLogger(ApplicationStartupBean.class);
            verify();
            LOGGER.info("application startup suceeded. ");
        } catch (Exception e) {
            if (LOGGER == null) {
                System.err.println("Exception while loading app. APP WILL NOT WORK CORRECTLY! CHECK log4j properties");
            } else {
                LOGGER.fatal("Verify application failed, check exception and config values", e);
            }
        }


    }
}
