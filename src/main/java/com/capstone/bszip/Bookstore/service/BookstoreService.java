package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import com.capstone.bszip.Member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.capstone.bszip.Bookstore.domain.BookstoreCategory.CHILD;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    private final RedisTemplate<String,String> redisTemplate;

    @Transactional
    public List<BookstoreResponse> searchBookstores(String keyword, Member member) {
        List<Bookstore> bookstores = bookstoreRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword);

        return bookstores.stream()
                .map(Bookstore -> convertToBookstoreResponse(Bookstore, member))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookstoreResponse> getBookstoresByCategory(BookstoreCategory category, Member member){
        if(category == null){
            List <Bookstore> bookstores = bookstoreRepository.findAll();
            return bookstores.stream()
                    .map(Bookstore -> convertToBookstoreResponse(Bookstore,member))
                    .collect(Collectors.toList());
        }
        List <Bookstore> bookstores =bookstoreRepository.findByBookstoreCategory(category);
        return bookstores.stream()
                .map(Bookstore -> convertToBookstoreResponse(Bookstore,member))
                .collect(Collectors.toList());
    }
    private BookstoreResponse convertToBookstoreResponse (Bookstore bookstore,Member member){
        String addressExceptCode = bookstore.getAddress();
        if(bookstore.getBookstoreCategory()!=CHILD) {
            addressExceptCode =addressExceptCode.substring(8);
        }
        boolean isLiked;
        if(member != null){
            isLiked = checkIfBookstoreLiked(member.getMemberId(),bookstore.getBookstoreId());
        }else{ //로그인 안한 사용자
            isLiked = false;
        }

        return new BookstoreResponse(
                bookstore.getBookstoreId(),
                bookstore.getName(),
                bookstore.getRating(),
                bookstore.getBookstoreCategory(),
                addressExceptCode,
                isLiked
        );
    }
    private boolean checkIfBookstoreLiked(Long memberId, Long bookstoreId) {
        String memberKey = "member:liked:bookstores:" + memberId;
        return redisTemplate.opsForSet().isMember(memberKey, bookstoreId.toString());
    }

    @Transactional
    public void toggleLikeBookstore(Long memberId, Long bookstoreId){
        String memberKey = "member:liked:bookstores:" + memberId;
        String bookstoreKey = "bookstore:likes:" + bookstoreId;

        if(redisTemplate.opsForSet().isMember(memberKey,bookstoreId.toString())){
            redisTemplate.opsForSet().remove(memberKey,bookstoreId.toString());//찜 취소
            redisTemplate.opsForValue().decrement(bookstoreKey); //찜한 수 -1
        }
        else{
            redisTemplate.opsForSet().add(memberKey,bookstoreId.toString());//찜
            redisTemplate.opsForValue().increment(bookstoreKey); //찜한 수 +1
        }

    }

    @Transactional
    public List<BookstoreResponse> getLikedBookstores(Member member){
        String memberKey = "member:liked:bookstores:" + member.getMemberId();
        Set<String> bookstoreIds = redisTemplate.opsForSet().members(memberKey);

        List<Long> longBookstoreIds = bookstoreIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List <Bookstore> bookstores = bookstoreRepository.findAllById(longBookstoreIds);

        return bookstores.stream()
                .map(Bookstore -> convertToBookstoreResponse(Bookstore,member))
                .collect(Collectors.toList());

    }

}
