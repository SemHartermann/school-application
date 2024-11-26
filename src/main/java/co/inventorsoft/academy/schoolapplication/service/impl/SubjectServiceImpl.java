package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.entity.Subject;
import co.inventorsoft.academy.schoolapplication.mapper.SubjectMapper;
import co.inventorsoft.academy.schoolapplication.repository.SubjectRepository;
import co.inventorsoft.academy.schoolapplication.service.SubjectService;
import co.inventorsoft.academy.schoolapplication.util.CSVUtils;
import co.inventorsoft.academy.schoolapplication.util.ExcelUtil;
import co.inventorsoft.academy.schoolapplication.util.validation.validator.SubjectValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    SubjectRepository subjectRepository;
    SubjectMapper subjectMapper = SubjectMapper.MAPPER;

    @Override
    public Page<SubjectDto> getAllSubjects(Pageable pageable) {
        Page<Subject> subjects = subjectRepository.findAll(pageable);
        return subjects.map(subjectMapper::toSubjectDto);
    }

    @Override
    public SubjectDto getSubjectById(Long id) {
        return subjectMapper.toSubjectDto(subjectRepository
                .findById(id)
                .orElseThrow());
    }

    @Override
    public void addNewSubject(SubjectDto subjectDto) {
        subjectRepository.save(subjectMapper
                .toSubjectEntity(subjectDto));
    }

    @Override
    public void updateSubjectById(Long id, SubjectDto requestBody) {
        Subject subject = subjectRepository
                .findById(id)
                .orElseThrow();

        subject.setName(requestBody.getName());

        subjectRepository.save(subject);
    }

    @Override
    public void deleteSubjectById(Long id) {
        subjectRepository.deleteById(id);
    }

    @Override
    public void uploadFromFile(MultipartFile file) {

        String fileType = FileNameUtils.getExtension(file.getOriginalFilename());
        List<SubjectDto> subjectDtoList;

        subjectDtoList = fileType.equals("csv") ?
                CSVUtils.convertToModel(file, SubjectDto.class)
                : ExcelUtil.excelDataToEntityList(file, SubjectDto.class, new SubjectValidator());

        subjectRepository.saveAll(subjectDtoList.stream()
                .map(SubjectMapper.MAPPER::toSubjectEntity)
                .collect(Collectors.toList()));
    }
}
