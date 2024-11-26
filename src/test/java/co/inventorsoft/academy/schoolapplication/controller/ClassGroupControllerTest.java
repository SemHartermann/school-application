package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupDto;
import co.inventorsoft.academy.schoolapplication.dto.ResponseClassGroupDto;
import co.inventorsoft.academy.schoolapplication.service.ClassGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ClassGroupControllerTest {
    @MockBean
    private ClassGroupService classGroupService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void createClassGroupTest() throws Exception {
        RequestClassGroupDto requestDto = new RequestClassGroupDto();
        requestDto.setName("10-A");
        ResponseClassGroupDto responseDto = new ResponseClassGroupDto();
        responseDto.setName("10-A");

        when(classGroupService.createClassGroup(any(RequestClassGroupDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/classes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(responseDto.getName()));

    }

    @Test
    @WithMockUser
    void getByIdTest() throws Exception {
        ResponseClassGroupDto responseDto = new ResponseClassGroupDto();
        responseDto.setName("10-A");
        responseDto.setId(1L);

        when(classGroupService.getClassGroupById(anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/classes/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(responseDto)));
    }

    @Test
    @WithMockUser
    void getAllClassGroupsTest() throws Exception {
        ResponseClassGroupDto responseDto1 = new ResponseClassGroupDto();
        responseDto1.setName("10-A");
        responseDto1.setId(1L);
        ResponseClassGroupDto responseDto2 = new ResponseClassGroupDto();
        responseDto2.setName("10-B");
        responseDto2.setId(2L);

        List<ResponseClassGroupDto> dtoList = Arrays.asList(responseDto1, responseDto2);
        Page<ResponseClassGroupDto> dtoPage = new PageImpl<>(dtoList);

        when(classGroupService.getAll(any(Pageable.class)))
                .thenReturn(dtoPage);

        mockMvc.perform(get("/api/classes")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(dtoList.size())));
    }

    @Test
    @WithMockUser
    void updateClassGroupTest() throws Exception {
        RequestClassGroupDto requestDto = new RequestClassGroupDto();
        ResponseClassGroupDto responseDto = new ResponseClassGroupDto();

        when(classGroupService.update(any(RequestClassGroupDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/classes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(responseDto.getName()));
    }

    @Test
    @WithMockUser
    void addStudentToClassGroupTest() throws Exception {
        doNothing().when(classGroupService).addStudentToClassGroup(anyLong(), anyLong());

        mockMvc.perform(put("/api/classes/1/2")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteClassGroupTest() throws Exception {
        doNothing().when(classGroupService).delete(anyLong());

        mockMvc.perform(delete("/api/classes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}