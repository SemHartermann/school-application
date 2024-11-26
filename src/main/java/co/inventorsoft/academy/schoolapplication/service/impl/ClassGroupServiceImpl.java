package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupDto;
import co.inventorsoft.academy.schoolapplication.dto.ResponseClassGroupDto;
import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.mapper.ClassGroupMapper;
import co.inventorsoft.academy.schoolapplication.repository.ClassGroupRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.ClassGroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassGroupServiceImpl implements ClassGroupService {

    private final ClassGroupRepository classGroupRepository;

    private final StudentRepository studentRepository;

    private final ClassGroupMapper classGroupMapper = ClassGroupMapper.MAPPER;

    @Override
    @Transactional
    public ResponseClassGroupDto createClassGroup(RequestClassGroupDto classGroupDto) {

        return classGroupMapper.classGroupToResponseClassGroupDto(
                save(classGroupMapper.requestClassGroupDtoToClassGroup(classGroupDto)));
    }

    @Override
    @Transactional
    public ResponseClassGroupDto getClassGroupById(Long id) {

        return classGroupMapper.classGroupToResponseClassGroupDto(findById(id));
    }

    @Override
    @Transactional
    public Page<ResponseClassGroupDto> getAll(Pageable pageable) {

        return classGroupRepository.findAll(pageable)
                .map(classGroupMapper::classGroupToResponseClassGroupDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        long millis = System.currentTimeMillis();
        ClassGroup classGroup = findById(id);
        String name = classGroup.getName();

        name = String.format("%s_%s", name, millis);

        classGroup.setName(name);
        classGroup.setDeleted(true);

        save(classGroup);
    }

    @Override
    @Transactional
    public ResponseClassGroupDto update(RequestClassGroupDto classGroupDto) {

        ClassGroup existedClassGroup = findById(classGroupDto.getId());
        existedClassGroup.setName(classGroupDto.getName());
        return classGroupMapper.classGroupToResponseClassGroupDto(save(existedClassGroup));
    }

    @Override
    @Transactional
    public void addStudentToClassGroup(Long classGroupId, Long studentId) {

        ClassGroup classGroup = findById(classGroupId);
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new RuntimeException(String.format("Student with id:%s was not found", studentId)));
        classGroup.getStudents().add(student);

        classGroupRepository.save(classGroup);
    }

    private ClassGroup save(ClassGroup classGroup) {

        return classGroupRepository.save(classGroup);
    }

    private ClassGroup findById(Long id) {

        return classGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Class group with Id %s was not found", id)));
    }
}
