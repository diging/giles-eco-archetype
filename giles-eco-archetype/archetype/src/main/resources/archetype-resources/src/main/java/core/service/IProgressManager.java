#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

import ${groupId}.gilesecosystem.requests.IImageExtractionRequest;

public interface IProgressManager {

    void startNewRequest(IImageExtractionRequest request);

    void setTotalPages(int total);

    void updateCurrentPage(int currentPage);

    void setPhase(ProgressPhase phase);

    ProgressPhase getPhase();

    IImageExtractionRequest getCurrentRequest();

    int getCurrentPage();

    int getTotalPages();

    void reset();

}