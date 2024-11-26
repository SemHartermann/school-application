package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupDto;
import co.inventorsoft.academy.schoolapplication.dto.ResponseClassGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassGroupService {

  ResponseClassGroupDto createClassGroup(RequestClassGroupDto classGroupDto);

  ResponseClassGroupDto getClassGroupById(Long id);

  Page<ResponseClassGroupDto> getAll(Pageable pageable);

  void delete(Long id);

  ResponseClassGroupDto update(RequestClassGroupDto classGroupDto);

  void addStudentToClassGroup(Long classGroupId, Long studentId);
}
