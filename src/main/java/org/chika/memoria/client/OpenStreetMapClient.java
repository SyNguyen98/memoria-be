package org.chika.memoria.client;

import org.chika.memoria.client.models.OpenStreetMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "OpenStreetMapClient", url = "https://nominatim.openstreetmap.org")
public interface OpenStreetMapClient {

    @GetMapping(value = "/reverse?format=jsonv2")
    ResponseEntity<OpenStreetMap> reverse(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude,
                                          @RequestHeader("User-Agent") String userAgent);

    default OpenStreetMap reverseGeocode(double latitude, double longitude) {
        return reverse(latitude, longitude, "MEMORIA/V1").getBody();
    }
}
