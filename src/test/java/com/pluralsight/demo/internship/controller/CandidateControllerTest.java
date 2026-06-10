package com.pluralsight.demo.internship.controller;

import com.pluralsight.demo.internship.model.Candidate;
import com.pluralsight.demo.internship.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CandidateService candidateService;

    @Test
    void getAllCandidates_shouldReturnListOfCandidates() throws Exception {
        // ARRANGE: Set up test data
        Candidate candidate1 = new Candidate("Emma Megan","emma@example.com", "Software Development");
        candidate1.setRegisteredAt(LocalDateTime.parse("2026-06-10T13:42:55"));
        candidate1.setVisible(true);
        candidate1.setId(1L);

        Candidate candidate2 = new Candidate("Sahar Omer","sahar.omer@example.com", "App Dev");
        candidate2.setId(2L);
        candidate2.setVisible(true);
        candidate2.setRegisteredAt(LocalDateTime.parse("2025-12-10T13:42:55"));

        List<Candidate> candidates = Arrays.asList(candidate1, candidate2);

        // Tell mock service what to return
        when(candidateService.getAllCandidates()).thenReturn(candidates);

        // ACT & ASSERT: Make request and verify response
        mockMvc.perform(get("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 OK
                .andExpect(jsonPath("$[0].name").value("Emma Megan"))
                .andExpect(jsonPath("$[0].fieldOfStudy").value("Software Development"))
                .andExpect(jsonPath("$[1].email").value("sahar.omer@example.com"))
                .andExpect(jsonPath("$[1].registeredAt").value("2025-12-10T13:42:55"))
                .andExpect(jsonPath("$.length()").value(2));  // 2 items
    }

    @Test
    void createCandidate_shouldReturnCreatedCandidate() throws Exception {
        // ARRANGE


        Candidate savedInternship = new Candidate("New Candidate", "New Email",
                "New Field");
        savedInternship.setId(10L);
        savedInternship.setVisible(false);
        savedInternship.setRegisteredAt(LocalDateTime.parse("2026-06-10T13:42:55"));

        when(candidateService.createCandidate(any(Candidate.class)))
                .thenReturn(savedInternship);

        // ACT & ASSERT
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "New Candidate",
                          "email": "New Email",
                          "fieldOfStudy": "New Field"
                        }
                        """))
                .andExpect(status().isOk())  // Should be 201 but our code returns 200
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("New Candidate"))
                .andExpect(jsonPath("$.email").value("New Email"))
                .andExpect(jsonPath("$.fieldOfStudy").value("New Field"))
                .andExpect(jsonPath("$.visible").value(false))
                .andExpect(jsonPath("$.registeredAt").value("2026-06-10T13:42:55"));
    }

    @Test
    void deleteCandidate_shouldReturnNoContent() throws Exception {
        // ARRANGE
        Long id = 5L;
        doNothing().when(candidateService).deleteCandidate(id);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/candidates/{id}", id))
                .andExpect(status().isNoContent());  // 204

        // Verify service was called
        verify(candidateService, times(1)).deleteCandidate(id);
    }
    @Test
    void getCandidateById_shouldReturnTheCandidate() throws Exception {
        // ARRANGE: Set up test data


        Long id = 5L;
        Candidate candidate = new Candidate("Sahar Omer","sahar.omer@example.com", "App Dev");
        candidate.setId(id);
        candidate.setVisible(true);
        candidate.setRegisteredAt(LocalDateTime.parse("2025-12-10T13:42:55"));

        // Tell mock service what to return
        when(candidateService.getCandidateById(id)).thenReturn(candidate);

        // ACT & ASSERT: Make request and verify response
        mockMvc.perform(get("/api/candidates/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 OK
                .andExpect(jsonPath("$.name").value("Sahar Omer"))
                .andExpect(jsonPath("$.fieldOfStudy").value("App Dev"))
                .andExpect(jsonPath("$.email").value("sahar.omer@example.com"))
                .andExpect(jsonPath("$.registeredAt").value("2025-12-10T13:42:55"));// 2 items
    }

}

