package org.chika.memoria.repositories;

import org.chika.memoria.models.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    Page<Location> findAllByCollectionIdOrderByTakenYearDescTakenMonthDescTakenDayDescTakenTimeDesc(String collectionId, Pageable pageable);

    Optional<Location> findByDriveItemId(String driveItemId);

    void deleteAllByCollectionId(String collectionId);

    @Aggregation(pipeline = {
            "{ '$match': { 'collectionId': { '$in': ?0 } } }",
            "{ '$group': { '_id': '$takenYear' } }",
            "{ '$project': { 'takenYear': '$_id', '_id': 0 } }"
    })
    List<Integer> findDistinctTakenYearByCollectionIdIn(List<String> collectionIds);
}
