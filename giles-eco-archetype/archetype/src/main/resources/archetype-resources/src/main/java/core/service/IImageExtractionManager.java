#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

import ${groupId}.gilesecosystem.requests.ICompletedStorageRequest;

public interface IImageExtractionManager {

    /*
     * (non-Javadoc)
     * 
     * @see ${groupId}.gilesecosystem.cepheus.service.pdf.impl.
     * IImageExtractionManager ${symbol_pound}extractImages(${groupId}.gilesecosystem.requests
     * .IImageExtractionRequest)
     */
    void extractImages(ICompletedStorageRequest request);

}