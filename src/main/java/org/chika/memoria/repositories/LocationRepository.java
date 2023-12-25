package org.chika.memoria.repositories;

import org.chika.memoria.models.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findAllByCollectionIdOrderByTakenYearAscTakenMonthAscTakenDayAscTakenTimeAsc(String collectionId);
    Optional<Location> findByDriveItemId(String driveItemId);

    void deleteAllByCollectionId(String collectionId);

    boolean existsByDriveItemIdAndCollectionId(String driveItemId, String collectionId);

    List<Location> findAllByTakenYear(Integer takenYear);
}
