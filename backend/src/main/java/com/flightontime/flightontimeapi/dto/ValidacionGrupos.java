
package com.flightontime.flightontimeapi.dto;

import jakarta.validation.GroupSequence;

public interface ValidacionGrupos {
    interface Primero{}
    interface Segundo{}

    @GroupSequence({Primero.class, Segundo.class})
    public interface SecuenciaOrdenada{}
}