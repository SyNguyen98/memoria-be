package org.chika.memoria.repositories;

import org.chika.memoria.models.MsToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsTokenRepository extends MongoRepository<MsToken, String> {
}
