package com.project3.project3;

import com.project3.DataTypes.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationFactoryTest {

    @Mock
    private OrderAccess orderAccess;

    @InjectMocks
    private ObservationFactory observationFactory;

    private Patient patient;
    private PhenomenonType quantitativeType;
    private PhenomenonType qualitativeType;
    private Protocol protocol;
    private Phenomenon phenomenon;

    @BeforeEach
    void setUp() {
        patient = new Patient();

        quantitativeType = new PhenomenonType();
        quantitativeType.setKind(PhenomenonKind.QUANTITATIVE);
        quantitativeType.setAllowedUnits(Arrays.asList("kg", "cm"));

        qualitativeType = new PhenomenonType();
        qualitativeType.setKind(PhenomenonKind.QUALITATIVE);

        phenomenon = new Phenomenon();

        qualitativeType.setPhenomena(List.of(phenomenon));
        phenomenon.setPhenomenonType(qualitativeType);

        protocol = new Protocol();
    }

    @Test
    void createMeasurement_valid_shouldReturnMeasurement() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.findPhenomenonType(1)).thenReturn(quantitativeType);
        when(orderAccess.findProtocol(1)).thenReturn(protocol);

        // Act
        Measurement m = observationFactory.createMeasurement(
                1, 1, 70.0, "kg", 1, new Date()
        );

        // Assert
        assertNotNull(m);
        assertEquals(patient, m.getPatient());
        assertEquals(70.0, m.getAmount());
        assertEquals("kg", m.getUnit());
        assertEquals(protocol, m.getProtocol());
    }

    @Test
    void createMeasurement_differing_shouldReturnNull() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.findPhenomenonType(1)).thenReturn(qualitativeType);

        // Act
        Measurement m = observationFactory.createMeasurement(
                1, 1, 70.0, "kg", null, new Date()
        );

        // Assert
        assertNull(m);
    }

    @Test
    void createMeasurement_nullApplicability_shouldUseNow() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.findPhenomenonType(1)).thenReturn(quantitativeType);

        // Act
        Measurement m = observationFactory.createMeasurement(
                1, 1, 70.0, "kg", null, null
        );

        // Assert
        assertNotNull(m);
        assertNotNull(m.getApplicabilityTime());
    }

    @Test
    void createCategoryObservation_valid_shouldReturnObservation() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.getPhenomenaById(1)).thenReturn(phenomenon);
        when(orderAccess.findProtocol(1)).thenReturn(protocol);

        // Act
        CategoryObservation obs = observationFactory.createCategoryObservation(
                1, 1, Presence.PRESENT, 1, new Date()
        );

        // Assert
        assertNotNull(obs);
        assertEquals(patient, obs.getPatient());
        assertEquals(phenomenon, obs.getPhenomenon());
        assertEquals(Presence.PRESENT, obs.getPresence());
        assertEquals(protocol, obs.getProtocol());
    }

    @Test
    void createCategoryObservation_nullApplicability_shouldUseNow() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.getPhenomenaById(1)).thenReturn(phenomenon);

        // Act
        CategoryObservation obs = observationFactory.createCategoryObservation(
                1, 1, Presence.ABSENT, null, null
        );

        // Assert
        assertNotNull(obs);
        assertNotNull(obs.getApplicabilityTime());
    }
}
