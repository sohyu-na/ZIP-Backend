package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.Hashtag;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.repository.HashtagRepository;
import com.capstone.bszip.Bookstore.service.dto.HashtagResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final BookstoreRepository bookstoreRepository;
    private final HashtagRepository hashtagRepository;
    private final HashtagExtractorService hashtagExtractorService;

    @Transactional
    public void migrateAllBookstoresToHashtags(){
        List<Bookstore> bookstores = bookstoreRepository.findAll();
        for(Bookstore bookstore : bookstores){
            String description = bookstore.getDescription();
            if(description!=null && !description.isEmpty()){
                //해시태그 추출
                String extractedTag = hashtagExtractorService.extractHashtag(description);

                if (extractedTag != null && !extractedTag.isEmpty()) {
                    //존재하지 않는 경우
                    if (!hashtagRepository.existsByTag(extractedTag)) {
                        Hashtag hashtag = Hashtag.builder()
                                .bookstore(bookstore)
                                .tag(extractedTag)
                                .build();
                        hashtagRepository.save(hashtag);
                    }
                }
            }
        }
    }
    //@PostConstruct
    public void executeHashtagMigration() {
        migrateAllBookstoresToHashtags();
        System.out.println("모든 서점의 해시태그 추출 및 저장이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public List<HashtagResponse> getRandomHashtagsWithBookstoreId(int count) {
        return hashtagRepository.findRandomHashtags(count)
                .stream()
                .map(hashtag -> new HashtagResponse(
                        hashtag.getTag(),
                        hashtag.getBookstore().getBookstoreId()))
                .collect(Collectors.toList());
    }
}
