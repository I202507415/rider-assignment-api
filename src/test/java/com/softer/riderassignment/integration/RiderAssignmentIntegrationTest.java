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
import org.springframework.http.HttpMethod;
import java.util.List;

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

    @Test
    void shouldReturnAllAssignmentsIntegration() {

        // ARRANGE: registrar dos asignaciones en H2
        RiderAssignment first = new RiderAssignment(
                null,
                9002L,
                602L,
                AssignmentStatus.ASSIGNED,
                null
        );

        RiderAssignment second = new RiderAssignment(
                null,
                9003L,
                603L,
                AssignmentStatus.ASSIGNED,
                null
        );

        repository.saveAll(List.of(first, second));

        // ACT: realizar una petición GET real
        ResponseEntity<RiderAssignment[]> response =
                restTemplate.getForEntity(
                        "/api/assignments",
                        RiderAssignment[].class
                );

        // ASSERT: verificar la respuesta HTTP
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        // Verificar que devuelve dos asignaciones
        assertEquals(2, response.getBody().length);

        // Verificar que los registros existen en H2
        assertEquals(2, repository.count());

        // Verificar los datos recibidos
        List<Long> deliveryIds = java.util.Arrays.stream(
                        response.getBody()
                )
                .map(RiderAssignment::getDeliveryId)
                .toList();

        assertTrue(deliveryIds.contains(9002L));
        assertTrue(deliveryIds.contains(9003L));
    }

    @Test
    void shouldReturnAssignmentByIdIntegration() {

        RiderAssignment assignment = new RiderAssignment(
                null,
                9004L,
                604L,
                AssignmentStatus.ASSIGNED,
                null
        );

        RiderAssignment saved = repository.saveAndFlush(assignment);


        ResponseEntity<RiderAssignment> response =
                restTemplate.getForEntity(
                        "/api/assignments/" + saved.getId(),
                        RiderAssignment.class
                );


        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());


        assertEquals(
                saved.getId(),
                response.getBody().getId()
        );

        assertEquals(
                9004L,
                response.getBody().getDeliveryId()
        );

        assertEquals(
                604L,
                response.getBody().getRiderId()
        );

        assertEquals(
                AssignmentStatus.ASSIGNED,
                response.getBody().getStatus()
        );

        assertNotNull(response.getBody().getAssignedAt());


        assertTrue(repository.existsById(saved.getId()));
    }


}
