package org.chika.memoria.repositories;

import org.chika.memoria.constants.Tag;
import org.chika.memoria.models.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {

    List<Collection> findAllByOwnerEmailOrUserEmailsContains(String ownerEmail, String userEmail);

    Page<Collection> findAllByOwnerEmail(String ownerEmail, Pageable pageable);

    Page<Collection> findAllByOwnerEmailOrUserEmailsContains(String ownerEmail, String userEmails, Pageable pageable);

    Page<Collection> findAllByTagsInAndOwnerEmailOrUserEmailsContains(List<Tag> tags, String ownerEmail, String userEmails, Pageable pageable);

    boolean existsByIdAndOwnerEmail(String id, String ownerEmail);

    boolean existsByIdAndOwnerEmailOrUserEmailsContains(String id, String ownerEmail, String userEmail);

    @Aggregation(pipeline = {
            "{ '$match': { '$or': [ { 'ownerEmail': ?0 }, { 'userEmails': ?1 } ] } }",
            "{ '$project': { 'id': 1 } }"
    })
    List<String> findCollectionIdsByOwnerEmailOrUserEmailsContains(String ownerEmail, String userEmail);

    @Aggregation(pipeline = {
            "{ '$match': { 'ownerEmail': ?0 } }",
            "{ '$unwind': '$userEmails' }",
            "{ '$group': { '_id': '$userEmails' } }",
            "{ '$project': { 'userEmail': '$_id', '_id': 0 } }"
    })
    List<String> findDistinctUserEmailsByOwnerEmail(String ownerEmail);
}
