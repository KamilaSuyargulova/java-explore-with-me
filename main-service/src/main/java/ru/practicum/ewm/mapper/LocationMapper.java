package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {

    public static LocationDto mapToLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLatitude(location.getLat());
        locationDto.setLongitude(location.getLon());
        return locationDto;
    }

    public static Location mapToLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setLat(locationDto.getLatitude());
        location.setLon(locationDto.getLongitude());
        return location;
    }
}