package ru.practicum.ewm.server.service;

import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    void createHit(EndpointHitRequestDto endpointHitRequestDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}