#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import ${package}.core.service.IImageExtractionManager;
import ${groupId}.gilesecosystem.requests.FileType;
import ${groupId}.gilesecosystem.requests.ICompletedStorageRequest;
import ${groupId}.gilesecosystem.requests.impl.CompletedStorageRequest;
import ${groupId}.gilesecosystem.septemberutil.properties.MessageType;
import ${groupId}.gilesecosystem.septemberutil.service.ISystemMessageHandler;

@PropertySource("classpath:/config.properties")
public class RequestReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IImageExtractionManager manager;    
    
    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @KafkaListener(id="imogen.extraction", topics = "${symbol_dollar}{topic_storage_request_complete}")
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedStorageRequest request = null;
        try {
            request = mapper.readValue(message, CompletedStorageRequest.class);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not unmarshall request.", e, MessageType.ERROR);
            // FIXME: handle this case
            return;
        }
        
        if (request.getFileType() == FileType.PDF) {
            manager.extractImages(request);
        }
        logger.info("File not a pdf. Skip processing.");
        // otherwise do nothing (we only care about pdfs)
    }
}
