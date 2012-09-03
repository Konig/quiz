package ch.bd.qv.quiz;

import ch.bd.qv.quiz.config.ConfigKeys;
import ch.bd.qv.quiz.config.ConfigValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.ftlines.wicket.cdi.CdiConfiguration;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.time.Duration;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 *
 * @see ch.bd.qv.quiz.Start#main(String[])
 */
public class QuizApplication extends WebApplication {
    
    private static final Logger LOGGER = Logger.getLogger(QuizApplication.class);  
    @Inject
    @ConfigValue(ConfigKeys.RESOURCES_FOLDER)
    private String pathToResourceFolders;

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        //======== Enabling CDI ==========
        new CdiConfiguration(getBeanManager()).configure(this);
        //======= remove wicket specific tags ==========
        getMarkupSettings().setStripWicketTags(true);
        //======= Adding custom resource folders ==========
        Preconditions.checkArgument(!Strings.isNullOrEmpty(pathToResourceFolders), "path to resourcefolder cannot be null!");
        LOGGER.debug("path to resource folder: " + pathToResourceFolders);
        getResourceSettings().setResourcePollFrequency(Duration.minutes(5));
        getResourceSettings().addResourceFolder(pathToResourceFolders);
        //disable exception on unknown keys; 
        getResourceSettings().setThrowExceptionOnMissingResource(false);
        getResourceSettings().getStringResourceLoaders().add(new SimplePropertyResourceLoader(pathToResourceFolders));
        Preconditions.checkArgument(new File(pathToResourceFolders).exists());
        //======= mounts ============
        // admin page
        mountPage("/admin", AdminPage.class);
    }
    
    private BeanManager getBeanManager() {
        try {
            return (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        } catch (NamingException ne) {
            throw new IllegalStateException("cannot continue without beanmanager, check server. ARE YOU TRYING TO RUN THIS APP ON A SERVLET-ONLY CONTAINER?", ne);
        }
    }
}
