#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ${groupId}.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import ${groupId}.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import ${groupId}.gilesecosystem.util.files.IFileStorageManager;
import ${groupId}.gilesecosystem.util.files.impl.FileStorageManager;
import ${groupId}.gilesecosystem.util.properties.IPropertiesManager;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan({ "${package}",
        "${groupId}.simpleusers.core",
        "${groupId}.gilesecosystem.util.properties",
        "${groupId}.gilesecosystem.requests" })
@PropertySource("classpath:config.properties")
public class RootConfig {

    @Value("${symbol_dollar}{base_directory}")
    private String baseDirectory;
    
    @Value("${symbol_dollar}{file_folder}")
    private String fileFolder;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ISystemMessageHandler getMessageHandler() {
        return new SystemMessageHandler(
                propertyManager.getProperty(Properties.APPLICATION_ID));
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:locale/messages");
        source.setFallbackToSystemLocale(false);
        return source;
    }
    
    @Bean
    public IFileStorageManager fileStorageManager() {
        FileStorageManager storageManager = new FileStorageManager();
        storageManager.setBaseDirectory(baseDirectory);
        storageManager.setFileTypeFolder(fileFolder);
        return storageManager;
    }
}