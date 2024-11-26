package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsDto;
import co.inventorsoft.academy.schoolapplication.service.MedicalRecordsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalRecordsController {

    MedicalRecordsService medicalRecordsService;

    @GetMapping
    public Page<MedicalRecordsDto> getAllRecords(@PageableDefault Pageable pageable) {
        return medicalRecordsService.getAllRecords(pageable);
    }

    @GetMapping("/{id}")
    public MedicalRecordsDto getRecordsById(@PathVariable Long id) {
        return medicalRecordsService.getRecordsById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewRecordsByStudentId(@PathVariable Long id,
                                         @RequestBody @Valid MedicalRecordsDto medicalRecordsDto) {

        medicalRecordsService.addNewRecordsByStudentId(id, medicalRecordsDto);
    }

    @PutMapping("/{id}")
    public void updateRecordsByRecordsId(@PathVariable Long id,
                                         @RequestBody @Valid MedicalRecordsDto medicalRecordsDto) {

        medicalRecordsService.updateRecordsByRecordsId(id, medicalRecordsDto);
    }

    @DeleteMapping("/{id}")
    public void deleteRecordsById(@PathVariable Long id){
        medicalRecordsService.deleteRecordsByRecordsId(id);
    }

}
