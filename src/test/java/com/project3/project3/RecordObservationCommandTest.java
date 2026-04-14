package com.project3.project3;

import com.project3.Command.*;

import com.project3.DataTypes.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecordObservationCommandTest {

    private ObservationFactory observationFactory;
    private OrderAccess orderAccess;

    @BeforeEach
    void setUp() {
        observationFactory = mock(ObservationFactory.class);
        orderAccess = mock(OrderAccess.class);
    }


    @Test
    void execute_shouldCreateAndPersistMeasurement() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "12.5", "kg", "", "staff", "2026-04-14"
        };

        Measurement measurement = new Measurement();

        when(observationFactory.createMeasurement(
                anyInt(), anyInt(), anyDouble(), anyString(), any(), any(Date.class)
        )).thenReturn(measurement);

        doAnswer(invocation -> {
            Measurement m = invocation.getArgument(0);
            m.setStatus(ObservationStatus.ACTIVE);
            return null;
        }).when(orderAccess).addMeasurement(any(Measurement.class));

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "measurement");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).addMeasurement(any(Measurement.class));
    }


    @Test
    void execute_shouldCreateAndPersistCategoryObservation() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "present", "3", "2026-04-14", "staff"
        };

        CategoryObservation obs = new CategoryObservation();

        when(observationFactory.createCategoryObservation(
                anyInt(), anyInt(), any(), any(), any(Date.class)
        )).thenReturn(obs);

        doAnswer(invocation -> {
            CategoryObservation o = invocation.getArgument(0);
            o.setStatus(ObservationStatus.ACTIVE);
            return null;
        }).when(orderAccess).addCategoryObservation(any(CategoryObservation.class));

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "categoryobservation");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).addCategoryObservation(any(CategoryObservation.class));
    }


    @Test
    void execute_shouldNotPersist_whenFactoryReturnsNull() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "12.5", "kg", "", "staff", "2026-04-14"
        };

        when(observationFactory.createMeasurement(any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "measurement");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, never()).addMeasurement(any());
        assertNull(command.observationId);
    }


    @Test
    void execute_shouldHandleCategoryBranchOnly() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "absent", "3", "2026-04-14", "staff"
        };

        when(observationFactory.createCategoryObservation(any(), any(), any(), any(), any()))
                .thenReturn(new CategoryObservation());

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "categoryobservation");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess).addCategoryObservation(any());
        verify(orderAccess, never()).addMeasurement(any());
    }
}
