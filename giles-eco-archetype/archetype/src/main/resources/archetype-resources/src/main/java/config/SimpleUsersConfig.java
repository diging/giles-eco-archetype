#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;
import org.springframework.context.annotation.Configuration;

import ${groupId}.simpleusers.core.config.SimpleUsers;
import ${groupId}.simpleusers.core.config.SimpleUsersConfiguration;

@Configuration
public class SimpleUsersConfig implements SimpleUsersConfiguration {

    @Override
    public void configure(SimpleUsers simpleUsers) {
        
    }
}