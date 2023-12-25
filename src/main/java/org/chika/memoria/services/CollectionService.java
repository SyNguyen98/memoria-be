package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import org.chika.memoria.dtos.CreateCollectionDTO;
import org.chika.memoria.exceptions.BadRequestException;
import org.chika.memoria.exceptions.ResourceNotFoundException;
import org.chika.memoria.models.Collection;
import org.chika.memoria.repositories.CollectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    public Collection findById(final String userEmail, final String id) {
        final var collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", id));
        if (collection.getOwnerEmail().equals(userEmail)) {
            return collection;
        }
        throw new BadRequestException("You are not owner of this collection");
    }

    public List<Collection> findAllByOwnerEmail(final String ownerEmail) {
        return collectionRepository.findAllByOwnerEmailOrderByLastModifiedDateDesc(ownerEmail);
    }

    public List<Collection> findAllByOwnerEmailOrUserEmail(final String userEmail) {
        return collectionRepository.findAllByOwnerEmailOrUserEmailsContainsOrderByLastModifiedDateDesc(userEmail, userEmail);
    }

    @Transactional
    public Collection create(final String ownerEmail, final CreateCollectionDTO collectionDTO) {
        final Collection collection = collectionDTO.convert(ownerEmail);

//        final File file = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"), image.getOriginalFilename()));
//        image.transferTo(file);
//        final var item = microsoftGraphClient.uploadFile(COLLECTION_COVER_ITEM_ID, file.getName(), Files.readAllBytes(Path.of(file.getPath())));
//        final var coverImage = microsoftGraphClient.getDriveItemById(item.getId());
//        collection.setCoverImageId(coverImage.getId());
//        collection.setCoverImageUrl(coverImage.getDownloadUrl());

        return collectionRepository.save(collection);
    }

    @Transactional
    public Collection update(final String userEmail, final Collection collection) {
        if (collectionRepository.existsByIdAndOwnerEmail(collection.getId(), userEmail)) {
            return collectionRepository.save(collection);
        }
        throw new BadRequestException("You don't have permission to update this collection");
    }

    @Transactional
    public void deleteById(final String userEmail, final String id) {
        if (collectionRepository.existsByIdAndOwnerEmail(id, userEmail)) {
            collectionRepository.deleteById(id);
        } else {
            throw new BadRequestException("You don't have permission to delete this collection");
        }
    }
}
