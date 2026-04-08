package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.CompilationValidationException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.api.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {

        if (newCompilationDto.getTitle() == null) {
            throw new CompilationValidationException("Не указан title compilation");
        } else if (newCompilationDto.getPinned() == null) {
            throw new CompilationValidationException("Не указано поле pinned compilation");
        }
        Compilation compilation = CompilationMapper.mapNewCompilationDtoToCompilation(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            Set<Event> eventSet = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
            compilation.setEvents(eventSet);
        }
        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.mapToCompilationDto(savedCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException("Compilation с таким id = " + compId + " не найден"));

        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getEvents() != null) {
            Set<Event> eventSet = new HashSet<>(eventRepository.findAllById(updateRequest.getEvents()));
            compilation.setEvents(eventSet);
        }
        Compilation updateCompilation = compilationRepository.save(compilation);

        return CompilationMapper.mapToCompilationDto(updateCompilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException("Compilation с таким id = " + compId + " не найден"));
        compilationRepository.delete(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, page).getContent();
        } else {
            compilations = compilationRepository.findAll(page).getContent();
        }

        return compilations.stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException("Compilation с таким id = " + compId + " не найдено"));

        return CompilationMapper.mapToCompilationDto(compilation);
    }

}