package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;

public class StatClient extends BaseClient {

    @Autowired
    public StatClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> getStats(String path, String start, String end, Collection<String> uris, Boolean unique) {

        if (start != null && end != null && unique != null) {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique
            );
            return get("", parameters);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> saveHit(HitDto hitDto) {
        return post("/hit", hitDto);
    }
}
