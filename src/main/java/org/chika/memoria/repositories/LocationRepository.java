package org.chika.memoria.repositories;

import org.chika.memoria.models.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findAllByCollectionId(String collectionId);

    List<Location> findAllByCollectionIdIn(Collection<String> collectionIds);

    List<Location> findAllByTakenYearAndCollectionId(int takenYear, String collectionId);

    List<Location> findAllByTakenYearAndCollectionIdIn(int takenYear, Collection<String> collectionIds);

    Page<Location> findAllByCollectionId(String collectionId, Pageable pageable);

    Page<Location> findAllByCollectionIdIn(Collection<String> collectionIds, Pageable pageable);

    void deleteAllByCollectionId(String collectionId);

    @Aggregation(pipeline = {
            "{ '$match': { 'collectionId': { '$in': ?0 } } }",
            "{ '$group': { '_id': '$takenYear' } }",
            "{ '$project': { 'takenYear': '$_id', '_id': 0 } }"
    })
    List<Integer> findDistinctTakenYearByCollectionIdIn(List<String> collectionIds);
}
