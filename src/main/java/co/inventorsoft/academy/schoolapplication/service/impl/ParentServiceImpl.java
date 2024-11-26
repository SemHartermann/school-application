package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.ParentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.ParentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Parent;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.repository.ParentRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.ParentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ParentServiceImpl implements ParentService {
    ParentRepository parentRepository;
    StudentRepository studentRepository;

    @Override
    public ParentResponseDto addParent(ParentRequestDto parentRequestDto) {
        Parent parent = mapToEntity(parentRequestDto);
        Parent savedParent = parentRepository.save(parent);
        return mapToDto(savedParent);
    }

    @Override
    public Page<ParentResponseDto> getAllParents(Pageable pageable) {
        return parentRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public ParentResponseDto getParentById(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent with id: " + id + " not found"));

        return mapToDto(parent);
    }

    @Override
    public ParentResponseDto updateParentById(Long id, ParentRequestDto parentRequestDto) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent with id: " + id + " not found"));

        parent.setFirstName(parentRequestDto.firstName());
        parent.setLastName(parentRequestDto.lastName());
        parent.setEmail(parentRequestDto.email());

        List<Student> children = studentRepository.findByIds(parentRequestDto.childrenId());
        parent.setChildren(children);

        Parent updatedParent = parentRepository.save(parent);

        return mapToDto(updatedParent);
    }

    @Override
    public void deleteParentById(Long id) {
        parentRepository.findById(id).ifPresent(parent -> {
            long millis = System.currentTimeMillis();
            String email = parent.getEmail();

            email = String.format("%s_%s", email, millis);

            parent.setEmail(email);
            parent.setDeleted(true);

            parentRepository.save(parent);
        });
    }

    private Parent mapToEntity(ParentRequestDto parentRequestDto) {
        List<Student> children = studentRepository.findByIds(parentRequestDto.childrenId());
        return Parent.builder()
                .firstName(parentRequestDto.firstName())
                .lastName(parentRequestDto.lastName())
                .email(parentRequestDto.email())
                .children(children)
                .build();
    }

    private ParentResponseDto mapToDto(Parent parent) {
        List<Long> childrenIds = parent.getChildren().stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        return new ParentResponseDto(
                parent.getId(),
                parent.getFirstName(),
                parent.getLastName(),
                parent.getEmail(),
                childrenIds
        );
    }
}
