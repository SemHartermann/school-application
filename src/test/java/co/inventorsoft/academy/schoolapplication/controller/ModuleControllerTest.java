package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import co.inventorsoft.academy.schoolapplication.entity.Module;
import co.inventorsoft.academy.schoolapplication.mapper.ModuleMapper;
import co.inventorsoft.academy.schoolapplication.repository.ModuleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ModuleControllerTest {
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    ModuleRepository moduleRepository;
    ModuleMapper moduleMapper;


    @Autowired
    public ModuleControllerTest(MockMvc mockMvc,
                                 ObjectMapper objectMapper,
                                 ModuleRepository moduleRepository) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.moduleRepository = moduleRepository;
        this.moduleMapper = Mappers.getMapper(ModuleMapper.class);
    }

    @Test
    void givenSubject_whenGetAllModulesBySubjectId_thenReturnModules() throws Exception {

        List<Module> modules = Stream.of(
                getModuleDto("Module1"),
                getModuleDto("Module2"),
                getModuleDto("Module3"))
            .map(moduleMapper::toEntity)
            .toList();

        moduleRepository.saveAll(modules);

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/modules/subjects/" + 1L + "/modules")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].name")
                .value("Module1")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[1].name")
                .value("Module2")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[2].name")
                .value("Module3")
        );
    }

    @Test
    void givenModule_whenCreateModule_thenReturnModule() throws Exception {

        ModuleDto moduleDto = getModuleDto("Module1");

        String moduleJson = objectMapper.writeValueAsString(moduleDto);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/modules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduleJson)

        ).andExpect(MockMvcResultMatchers.status().isCreated());

        long newModuleId = 1;

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/modules/" + newModuleId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.name").value("Module1")
        );
    }

    @Test
    void givenModule_whenUpdateModule_thenReturnModule() throws Exception {

        ModuleDto moduleDto = getModuleDto("Module1");

        String moduleJson = objectMapper.writeValueAsString(moduleDto);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/modules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduleJson)

        ).andExpect(MockMvcResultMatchers.status().isCreated());

        moduleDto = getModuleDto("Module1 New");

        moduleDto.setId(1L);

        moduleJson = objectMapper.writeValueAsString(moduleDto);

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/modules/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduleJson)

        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/modules/" + moduleDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.name").value("Module1 New")
        );
    }

    @Test
    void givenModuleId_whenDeleteModule_thenReturnNothing() throws Exception {

        ModuleDto moduleDto = getModuleDto("Module1");

        String moduleJson = objectMapper.writeValueAsString(moduleDto);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/modules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduleJson)

        ).andExpect(MockMvcResultMatchers.status().isCreated());

        long newModuleId = 1;

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/modules/" + newModuleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduleJson)

        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/modules" + newModuleId)
                    .contentType(MediaType.APPLICATION_JSON)

            ).andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
    }

    private ModuleDto getModuleDto(String name) {
        return ModuleDto.builder()
            .name(name)
            .teacherId(1L)
            .classRoomId(1L)
            .subjectId(1L)
            .schedule(Map.of(DayOfWeek.MONDAY, new HashSet<>()))
            .endDate(ZonedDateTime.now())
            .startDate(ZonedDateTime.now().minusMonths(1))
            .build();
    }
}
