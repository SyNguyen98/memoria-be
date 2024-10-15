package org.chika.memoria.repositories;

import org.chika.memoria.models.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {

    Page<Collection> findAllByOwnerEmail(String ownerEmail, Pageable pageable);

    Page<Collection> findAllByOwnerEmailOrUserEmailsContainsOrderByLastModifiedDateDesc(String ownerEmail, String userEmail, Pageable pageable);

    boolean existsByIdAndOwnerEmail(String id, String ownerEmail);

    boolean existsByIdAndOwnerEmailOrUserEmailsContains(String id, String ownerEmail, String userEmail);
}
