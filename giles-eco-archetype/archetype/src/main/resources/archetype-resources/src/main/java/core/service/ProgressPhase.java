#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

public enum ProgressPhase {
    RAMP_UP,
    PROCESSING,
    WIND_DOWN,
    DONE,
    IDLE;
}
