package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(
                    compilation.getEvents().stream()
                            .map(EventMapper::eventMapToEventShortDto)
                            .toList()
            );
        }
        return compilationDto;
    }


    public static Compilation mapToCompilation(CompilationDto compilationDto, Event event) {
        Compilation compilation = new Compilation();
        compilation.setId(compilationDto.getId());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.getPinned());
        compilation.getEvents().add(event);

        return compilation;
    }

    public static CompilationDto mapNewCompilationDtoToCompilationDto(NewCompilationDto newCompilationDto, Event event) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setTitle(newCompilationDto.getTitle());
        compilationDto.setPinned(newCompilationDto.getPinned());
        compilationDto.getEvents().add(EventMapper.eventMapToEventShortDto(event));

        return compilationDto;
    }


    public static Compilation mapNewCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());

        return compilation;
    }

    public static Compilation mapUpdateCompilationRequestToCompilation(UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = new Compilation();
        compilation.setTitle(updateCompilationRequest.getTitle());
        compilation.setPinned(updateCompilationRequest.getPinned());

        return compilation;
    }

    public static CompilationDto mapUpdateCompilationRequestToCompilationDto(UpdateCompilationRequest updateCompilationRequest, Event event) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setTitle(updateCompilationRequest.getTitle());
        compilationDto.setPinned(updateCompilationRequest.getPinned());
        compilationDto.getEvents().add(EventMapper.eventMapToEventShortDto(event));

        return compilationDto;
    }
}