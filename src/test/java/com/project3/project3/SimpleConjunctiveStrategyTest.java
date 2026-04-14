package com.project3.project3;

import com.project3.DataTypes.*;
import com.project3.Strategy.SimpleConjunctiveStrategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleConjunctiveStrategyTest {

    private final SimpleConjunctiveStrategy strategy = new SimpleConjunctiveStrategy();


    @Test
    void evaluate_allArgumentsSatisfied_shouldReturnTrue() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        rule.setArgumentConcepts(new String[]{"Diabetes", "Hypertension"});
        rule.setProductConcept("Disease");

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        Phenomenon p2 = new Phenomenon();
        p2.setName("Hypertension");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.PRESENT);
        obs1.setStatus(ObservationStatus.ACTIVE);

        CategoryObservation obs2 = new CategoryObservation();
        obs2.setPhenomenon(p2);
        obs2.setPresence(Presence.PRESENT);
        obs2.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Arrays.asList(obs1, obs2);

        // ACT
        boolean result = strategy.evaluate(rule, observations);

        // ASSERT
        assertTrue(result);
    }


    @Test
    void evaluate_missingArgument_shouldReturnFalse() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        rule.setArgumentConcepts(new String[]{"Diabetes", "Hypertension"});

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.PRESENT);
        obs1.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Collections.singletonList(obs1);

        // ACT
        boolean result = strategy.evaluate(rule, observations);

        // ASSERT
        assertFalse(result);
    }


    @Test
    void evaluate_inactiveObservation_shouldReturnFalse() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        rule.setArgumentConcepts(new String[]{"Diabetes"});

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.PRESENT);
        obs1.setStatus(ObservationStatus.REJECTED); // inactive

        List<Observation> observations = Arrays.asList(obs1);

        // ACT
        boolean result = strategy.evaluate(rule, observations);

        // ASSERT
        assertFalse(result);
    }


    @Test
    void evaluate_absentPhenomenon_shouldReturnFalse() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        rule.setArgumentConcepts(new String[]{"Diabetes"});

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.ABSENT);
        obs1.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Arrays.asList(obs1);

        // ACT
        boolean result = strategy.evaluate(rule, observations);

        // ASSERT
        assertFalse(result);
    }


    @Test
    void evaluate_measurementMatch_shouldReturnTrue() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        rule.setArgumentConcepts(new String[]{"BloodPressure"});

        PhenomenonType type = new PhenomenonType();
        type.setName("BloodPressure");

        Measurement m = new Measurement();
        m.setPhenomenonType(type);
        m.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Arrays.asList(m);

        // ACT
        boolean result = strategy.evaluate(rule, observations);

        // ASSERT
        assertTrue(result);
    }
}
