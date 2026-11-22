package com.softer.riderassignment.integration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softer.riderassignment.model.AssignmentStatus;
import com.softer.riderassignment.model.RiderAssignment;
import com.softer.riderassignment.repository.RiderAssignmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class RiderAssignmentIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RiderAssignmentRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAssignmentIntegration() {

        RiderAssignment request = new RiderAssignment(
                null,
                9001L,
                601L,
                AssignmentStatus.ASSIGNED,
                null
        );

        ResponseEntity<RiderAssignment> response =
                restTemplate.postForEntity(
                        "/api/assignments",
                        request,
                        RiderAssignment.class
                );

        assertEquals(HttpStatus.CREATED,
                response.getStatusCode());

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        assertEquals(9001L,
                response.getBody().getDeliveryId());

        assertEquals(601L,
                response.getBody().getRiderId());

        assertEquals(1, repository.count());
    }
}
