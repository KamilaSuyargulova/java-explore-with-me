package ru.practicum.ewm.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.server.service.StatisticService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHits(@Valid @RequestBody EndpointHitRequestDto endpointHitRequestDto) {
        statisticService.createHit(endpointHitRequestDto);
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> getStats(
            @RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statisticService.getStats(start, end, uris, unique);
    }
}