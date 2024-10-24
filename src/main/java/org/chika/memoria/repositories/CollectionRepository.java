package org.chika.memoria.repositories;

import org.chika.memoria.models.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {

    Page<Collection> findAllByOwnerEmail(String ownerEmail, Pageable pageable);

    Page<Collection> findAllByOwnerEmailOrUserEmailsContains(String ownerEmail, String userEmail, Pageable pageable);

    Page<Collection> findAllByOwnerEmailOrUserEmailsContainsOrderByLastModifiedDateDesc(String ownerEmail, String userEmail, Pageable pageable);

    boolean existsByIdAndOwnerEmail(String id, String ownerEmail);

    boolean existsByIdAndOwnerEmailOrUserEmailsContains(String id, String ownerEmail, String userEmail);

    @Aggregation(pipeline = {
            "{ '$match': { 'ownerEmail': ?0 } }",
            "{ '$unwind': '$userEmails' }",
            "{ '$group': { '_id': '$userEmails' } }",
            "{ '$project': { 'userEmail': '$_id', '_id': 0 } }"
    })
    List<String> findDistinctUserEmailsByOwnerEmail(String ownerEmail);
}
