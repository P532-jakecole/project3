package com.project3.Managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project3.Command.Command;
import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.CommandLog;
import com.project3.DataTypes.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ObservationManager {
    private final OrderAccess orderAccess;
    private final ObservationFactory observationFactory;
    private ApplicationEventPublisher publisher;
    private final CommandLog commandLog;
    private final ObjectMapper mapper;

    public ObservationManager(OrderAccess orderAccess, ObservationFactory observationFactory, ApplicationEventPublisher publisher, CommandLog commandLog) {
        this.orderAccess = orderAccess;
        this.observationFactory = observationFactory;
        this.publisher = publisher;
        this.commandLog = commandLog;
        mapper = new ObjectMapper();


    }

    // Input string is [ patient , phenomenonTypes , value, unit, (optional)protocolId, staff, applicableTime ]
    public void recordMeasurement(Object[] inputs) throws JsonProcessingException {
        String staff = inputs[5].toString();
        RecordObservationCommand recordObservation = new RecordObservationCommand(inputs, observationFactory, orderAccess, staff, "measurement");
        recordObservation.execute();
        commandLog.addCommandLog(CommandType.RecordObservation, mapper.writeValueAsString(recordObservation), recordObservation.timestamp, recordObservation.staff);
        publisher.publishEvent(recordObservation);
    }

    // Input string is [ patientId, phenomenonId, presence, protocolId, applicabilityTime, staff ]
    public void recordCategoryObservation(Object[] inputs) throws JsonProcessingException {
        String staff = inputs[5].toString();
        RecordObservationCommand recordObservation = new RecordObservationCommand(inputs, observationFactory, orderAccess, staff, "categoryobservation");
        recordObservation.execute();
        commandLog.addCommandLog(CommandType.RecordObservation, mapper.writeValueAsString(recordObservation), recordObservation.timestamp, recordObservation.staff);
        publisher.publishEvent(recordObservation);
   }

    public void rejectObservation(Integer id, String reason, String staff) throws JsonProcessingException {
        RejectObservationCommand rejectObservation = new RejectObservationCommand(id, reason, orderAccess, staff);
        rejectObservation.execute();
        commandLog.addCommandLog(CommandType.RejectObservation, mapper.writeValueAsString(rejectObservation), rejectObservation.timestamp, rejectObservation.staff);
        publisher.publishEvent(rejectObservation);
   }

    public ArrayList<PhenomenonType> listPhenomenonTypes(){
        return orderAccess.findAllPhenomenonTypes();
    }

    // inputs: [ name, kind, allowedUnits ]
    @Transactional
    public void createPhenomenonType(Object[] inputs){
        PhenomenonType pt = new PhenomenonType();
        pt.setName((String) inputs[0]);

        switch (inputs[1].toString()){
            case "QUANTITATIVE":
                pt.setKind(PhenomenonKind.QUANTITATIVE);
                List<String> unitsList = (List<String>) inputs[2];
                pt.setAllowedUnits(unitsList);
                break;
            case "QUALITATIVE":
                pt.setKind(PhenomenonKind.QUALITATIVE);
                List<String> phenomenonList = (List<String>) inputs[2];
                List<Phenomenon> phenomenons = new ArrayList<>();
                for(String s : phenomenonList) {
                    Phenomenon phen = new Phenomenon();
                    phen.setName(s);
                    phen.setPhenomenonType(pt);
                    phenomenons.add(phen);
                    orderAccess.addPhenomenon(phen);
                }
                pt.setPhenomena(phenomenons);
                break;
        }
        orderAccess.addPhenomenonType(pt);
    }

}
