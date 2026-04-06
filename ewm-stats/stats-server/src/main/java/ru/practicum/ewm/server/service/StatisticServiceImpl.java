package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void createHit(EndpointHitRequestDto endpointHitRequestDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitRequestDto.getApp());
        endpointHit.setUri(endpointHitRequestDto.getUri());
        endpointHit.setIp(endpointHitRequestDto.getIp());
        endpointHit.setTimestamp(endpointHitRequestDto.getTimestamp());
        endpointHitRepository.save(endpointHit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return endpointHitRepository.getAllViewStats(start, end, uris, unique);
    }
}