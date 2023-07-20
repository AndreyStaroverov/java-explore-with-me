package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Hit;

import java.sql.Timestamp;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {

    @Query("select count(id) from Hit where uri = ?1 AND timestamp between ?2 and ?3")
    Long findAllByUri(String uri, Timestamp start, Timestamp end);

    @Query("select count(DISTINCT ip) from Hit where uri = ?1 AND timestamp between ?2 and ?3")
    Long findAllByUriUnique(String iri, Timestamp start, Timestamp end);

    @Query(value = "select DISTINCT uri from hits", nativeQuery = true)
    List<String> findAllForEmptyUris();

    Hit getHitByUri(String uri);
}
