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
import ch.bd.qv.quiz.config.ConfigValue;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 *
 * @author thierry
 */
@Dependent
public class LocaleProducer {

    
    private static final Logger LOGGER = Logger.getLogger(LocaleProducer.class);
    
    @Inject
    @ConfigValue(ConfigKeys.SUPPORTED_LANGUAGES)
    private String langs;

    @Produces
    public List<Locale> getLocales() {
        List<Locale> locales = new ArrayList<> ();
        LOGGER.debug("languages configured: "+langs);
        String[] args = langs.split(",");
        for (String lang : args) {
            switch (lang.toLowerCase()) {
                case "de":
                case "fr":
                case "it":
                    locales.add(new Locale(lang, "ch"));
                    break; 
                default:
                    locales.add(new Locale(lang));
            }
        }
        LOGGER.debug("languages detected: "+Joiner.on(":").join(locales));
        return locales;
    }
}
