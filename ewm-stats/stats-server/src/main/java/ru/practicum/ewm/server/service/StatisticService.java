package ru.practicum.ewm.server.service;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    void createHit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}