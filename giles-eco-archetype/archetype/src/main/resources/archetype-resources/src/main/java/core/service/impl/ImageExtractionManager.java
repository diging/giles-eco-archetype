#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${package}.api.DownloadFileController;
import ${package}.config.Properties;
import ${package}.core.service.IImageExtractionManager;
import ${package}.core.service.IProgressManager;
import ${package}.core.service.ProgressPhase;
import ${groupId}.gilesecosystem.requests.ICompletedStorageRequest;
import ${groupId}.gilesecosystem.requests.ICompletionNotificationRequest;
import ${groupId}.gilesecosystem.requests.IRequestFactory;
import ${groupId}.gilesecosystem.requests.PageStatus;
import ${groupId}.gilesecosystem.requests.RequestStatus;
import ${groupId}.gilesecosystem.requests.exceptions.MessageCreationException;
import ${groupId}.gilesecosystem.requests.impl.CompletionNotificationRequest;
import ${groupId}.gilesecosystem.requests.impl.PageElement;
import ${groupId}.gilesecosystem.requests.kafka.IRequestProducer;
import ${groupId}.gilesecosystem.septemberutil.properties.MessageType;
import ${groupId}.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import ${groupId}.gilesecosystem.util.files.IFileStorageManager;
import ${groupId}.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class ImageExtractionManager extends AExtractionManager implements IImageExtractionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private IFileStorageManager fileStorageManager;

    @Autowired
    private IRequestFactory<ICompletionNotificationRequest, CompletionNotificationRequest> requestFactory;

    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IProgressManager progressManager;
    
    @PostConstruct
    public void init() {
        /*
         * Recommended fix for performance issues due to colors: "Due to the change of
         * the java color management module towards “LittleCMS”, users can experience
         * slow performance in color operations. Solution: disable LittleCMS in favour
         * of the old KCMS (Kodak Color Management System)"
         */
        //System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        requestFactory.config(CompletionNotificationRequest.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ${groupId}.gilesecosystem.cepheus.service.pdf.impl.
     * IImageExtractionManager ${symbol_pound}extractImages(${groupId}.gilesecosystem.requests
     * .IImageExtractionRequest)
     */
    @Override
    public void extractImages(ICompletedStorageRequest request) {
        logger.info("Extracting images for: " + request.getDownloadUrl());
        
        PDDocument pdfDocument = null;
        RequestStatus status = RequestStatus.COMPLETE;
        try {
            pdfDocument = Loader.loadPDF(new ByteArrayInputStream(downloadFile(request.getDownloadUrl())));
        } catch (IOException e) {
            messageHandler.handleMessage("Could not extract text.", e, MessageType.ERROR);
            status = RequestStatus.FAILED;
        }

        PdfProcessor processor = new PdfProcessor(fileStorageManager, request);
        List<${groupId}.gilesecosystem.requests.impl.Page> pages = new ArrayList<>();
        if (pdfDocument != null) {
            
            
            int numPages = pdfDocument.getNumberOfPages();
            
            String restEndpoint = getRestEndpoint();

            for (int i = 0; i < numPages; i++) {
                ${groupId}.gilesecosystem.requests.impl.Page requestPage = new ${groupId}.gilesecosystem.requests.impl.Page();
                requestPage.setPageNr(i);
                requestPage.setPageElements(new ArrayList<>());

                try {
                    PDPage page = pdfDocument.getPage(i);
                    
                    // saves images in page to filesystem
                    processor.processPage(page);
                    
                    
                    for (String filename : processor.getImageFilenames()) {
                        PageElement pageElem = new PageElement();
                        pageElem.setContentType("image/png");
                        pageElem.setFilename(filename);
                        pageElem.setType("IMAGE");
                        pageElem.setDownloadUrl(
                                restEndpoint + DownloadFileController.GET_FILE_URL
                                        .replace(
                                                DownloadFileController.REQUEST_ID_PLACEHOLDER,
                                                request.getRequestId())
                                        .replace(
                                                DownloadFileController.DOCUMENT_ID_PLACEHOLDER,
                                                request.getDocumentId())
                                        .replace(DownloadFileController.FILENAME_PLACEHOLDER,
                                                filename));
                        pageElem.setStatus(PageStatus.COMPLETE);
                        requestPage.getPageElements().add(pageElem);
                    }
                    
                } catch (IOException | RuntimeException e) {
                    messageHandler.handleMessage("Could not render image.", e, MessageType.ERROR);
                    requestPage.setStatus(PageStatus.FAILED);
                    requestPage.setErrorMsg(e.getMessage());
                }

                // this needs to be reset for every page
                processor.resetFilenames();
                pages.add(requestPage);
            }
            
            try {
                pdfDocument.close();
            } catch (IOException e) {
                messageHandler.handleMessage("Error closing document.", e, MessageType.ERROR);
            }
        }

        progressManager.setPhase(ProgressPhase.WIND_DOWN);
        ICompletionNotificationRequest completedRequest = null;
        try {
            completedRequest = requestFactory.createRequest(request.getRequestId(), request.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
            // this should never happen if used correctly
        }

        completedRequest.setDocumentId(request.getDocumentId());
        completedRequest.setFileId(request.getFileId());
        completedRequest.setNotifier(propertiesManager.getProperty(Properties.NOTIFIER_ID));
        completedRequest.setStatus(status);
        completedRequest.setExtractionDate(OffsetDateTime.now(ZoneId.of("UTC")).toString());
        completedRequest.setPages(pages);

        progressManager.setPhase(ProgressPhase.DONE);
        try {
            requestProducer.sendRequest(completedRequest,
                    propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        } catch (MessageCreationException e) {
            messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
        }
        
        progressManager.reset();
    }

    
}
