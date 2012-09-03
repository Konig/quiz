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
package ch.bd.qv.quiz;

import com.google.common.io.Closeables;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;

/**
 *
 * load properties from property files without any path structure
 * @author thierry
 */
public class SimplePropertyResourceLoader implements IStringResourceLoader {
    private final String path;
    private static final Logger LOGGER = Logger.getLogger(SimplePropertyResourceLoader.class);

    public SimplePropertyResourceLoader(String path) {
        this.path = path; 
    }

    @Override
    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
        return lookup(key, locale);
    }

    @Override
    public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
        return lookup(key, locale);
    }

    private String lookup(String key, Locale locale) {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            File file = new File(path + "/QuizTranslation_" + locale.getLanguage() + ".properties");
            fis = new FileInputStream(file);
            props.load(fis);
            //                    LOGGER.debug("looking for "+key + " in file: "+file.getAbsolutePath());
            return (String) props.get(key);
        } catch (IOException ioex) {
            LOGGER.warn("cannot access file with locale " + locale, ioex);
            return null;
        } finally {
            Closeables.closeQuietly(fis);
        }
    }
    
}
