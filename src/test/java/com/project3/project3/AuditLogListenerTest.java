package com.project3.project3;

import com.project3.CommandLog;
import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.Observer.AuditLogListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogListenerTest {

    @Mock
    private CommandLog commandLog;

    @InjectMocks
    private AuditLogListener auditLogListener;


    @Test
    void onObservationCreated_shouldWriteRecordAudit() {
        // ARRANGE
        RecordObservationCommand cmd = mock(RecordObservationCommand.class);
        cmd.observationId = 1;
        cmd.patientId = 10;
        cmd.timestamp = new Date();

        // ACT
        auditLogListener.onObservationCreated(cmd);

        // ASSERT
        verify(commandLog, times(1))
                .addAuditLogString(eq("Record"), eq(1), eq(10), eq(cmd.timestamp));
    }


    @Test
    void onObservationRejection_shouldWriteRejectedAudit() {
        // ARRANGE
        RejectObservationCommand cmd = mock(RejectObservationCommand.class);
        cmd.observationId = 2;
        cmd.patientId = 20;
        cmd.timestamp = new Date();

        // ACT
        auditLogListener.onObservationRejection(cmd);

        // ASSERT
        verify(commandLog, times(1))
                .addAuditLogString(eq("Rejected"), eq(2), eq(20), eq(cmd.timestamp));
    }
}
