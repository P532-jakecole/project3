package com.project3.Command;

import com.project3.DataTypes.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class RecordObservationCommand implements Command {
    public Integer patientId;
    private final ObservationFactory observationFactory;
    private final OrderAccess orderAccess;
    public String staff;
    public String type;
    public Object[] inputs;
    public Date timestamp;

    public Integer observationId;

    public RecordObservationCommand(Object[] inputs, ObservationFactory observationFactory, OrderAccess orderAccess,String staff, String type) {
        patientId = Integer.parseInt(inputs[0].toString());
        this.observationFactory = observationFactory;
        this.orderAccess = orderAccess;
        this.staff = staff;
        this.inputs = inputs;
        this.type = type.toLowerCase();
        this.timestamp = new Date();
    }
    @Override
    public void execute() {
        if(type == "measurement"){
            LocalDate localDate = LocalDate.parse(inputs[6].toString());
            Integer protocolId = null;
            if(inputs[4] != ""){
                protocolId = Integer.parseInt(inputs[4].toString());
            }
            Measurement observation = observationFactory.createMeasurement(patientId, Integer.parseInt(inputs[1].toString()), Double.parseDouble(inputs[2].toString()), (String) inputs[3], protocolId, java.sql.Date.valueOf(localDate));
            if(observation != null){
                orderAccess.addMeasurement(observation);
                observationId = observation.getId();
            }
        }else{
            LocalDate localDate = LocalDate.parse(inputs[4].toString());
            Presence presence = null;
            if(inputs[2].toString().equalsIgnoreCase("present")){
                presence = Presence.PRESENT;
            }else if(inputs[2].toString().equalsIgnoreCase("absent")){
                presence = Presence.ABSENT;
            }
            Integer protocolId = null;
            if(inputs[3] != ""){
                protocolId = Integer.parseInt(inputs[4].toString());
            }
            CategoryObservation observation = observationFactory.createCategoryObservation(Integer.parseInt(inputs[0].toString()), Integer.parseInt(inputs[1].toString()), presence, protocolId, java.sql.Date.valueOf(localDate));
            if(observation != null){
                orderAccess.addCategoryObservation(observation);
                observationId = observation.getId();
            }
        }
    }

    @Override
    public void undo() {

    }
}
