package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ParentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.ParentResponseDto;
import co.inventorsoft.academy.schoolapplication.service.impl.ParentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ParentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ParentServiceImpl parentService;

    @Test
    void testGetAllParents() throws Exception {
        Page<ParentResponseDto> page = new PageImpl<>(List.of(
                new ParentResponseDto(1L, "First", "First", "email@gmail.com", List.of(1L)),
                new ParentResponseDto(2L, "Second", "Second", "second@gmail.com", List.of(2L))
        ));

        when(parentService.getAllParents(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/parents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(parentService).getAllParents(any(Pageable.class));
    }

    @Test
    void testAddNewParent() throws Exception {
        ParentRequestDto parentRequest = new ParentRequestDto("Name", "Surname", "email@gmail.com", List.of(1L));
        ParentResponseDto parentResponse = new ParentResponseDto(1L, "Name", "Surname", "email@gmail.com", List.of(1L));

        when(parentService.addParent(parentRequest)).thenReturn(parentResponse);

        mockMvc.perform(post("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(parentResponse.id()))
                .andExpect(jsonPath("$.firstName").value(parentResponse.firstName()));

        verify(parentService).addParent(parentRequest);
    }

    @Test
    void testGetParentById() throws Exception {
        Long parentId = 1L;

        ParentResponseDto parentResponseDto = new ParentResponseDto(parentId, "Parent First Name", "Last name", "email@gmal.com", List.of(1L));

        when(parentService.getParentById(parentId)).thenReturn(parentResponseDto);

        mockMvc.perform(get("/api/parents/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(parentResponseDto.id()));

        verify(parentService).getParentById(1L);
    }

    @Test
    void testDeleteParent() throws Exception {
        mockMvc.perform(delete("/api/parents/1"))
                .andExpect(status().isNoContent());

        verify(parentService).deleteParentById(1L);
    }

    @Test
    void testUpdateParentById() throws Exception {
        Long parentId = 1L;

        ParentRequestDto parentRequestDto = new ParentRequestDto("Updated_Parent", "Updated_Surname", "email@gmail.com", List.of(1L));
        ParentResponseDto updatedParent = new ParentResponseDto(parentId, "Updated_Parent", "Updated_Surname", "email@gmail.com", List.of(1L));

        when(parentService.updateParentById(parentId, parentRequestDto)).thenReturn(updatedParent);

        mockMvc.perform(put("/api/parents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedParent.id()))
                .andExpect(jsonPath("$.firstName").value(updatedParent.firstName()));

        verify(parentService).updateParentById(parentId, parentRequestDto);
    }
}