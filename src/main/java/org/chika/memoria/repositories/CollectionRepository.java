package org.chika.memoria.repositories;

import org.chika.memoria.models.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {

    List<Collection> findAllByOwnerEmailOrderByLastModifiedDateDesc(String ownerEmail);
    List<Collection> findAllByOwnerEmailOrUserEmailsContainsOrderByLastModifiedDateDesc(String ownerEmail, String userEmail);
    boolean existsByIdAndOwnerEmail(String id, String ownerEmail);
    boolean existsByIdAndOwnerEmailOrUserEmailsContains(String id, String ownerEmail, String userEmail);
}
