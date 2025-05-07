package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.service.dto.BookstoreDetailResponse;
import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import com.capstone.bszip.Bookstore.service.dto.Hours;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.capstone.bszip.Bookstore.domain.BookstoreCategory.CHILD;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    private final RedisTemplate<String,String> redisTemplate;

    @Transactional
    public List<BookstoreResponse> searchBookstores(String keyword, Member member,double lat, double lng) {
        List<Bookstore> bookstores = bookstoreRepository.findByNameOrAddressOrderByDistance(keyword, keyword,lat,lng);

        return bookstores.stream()
                .map(Bookstore -> convertToBookstoreResponse(Bookstore, member))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookstoreResponse> getBookstoresByCategory(BookstoreCategory category, Member member,double lat, double lng){
        if(category == null){
            List <Bookstore> bookstores = bookstoreRepository.findAllOrderByDistance(lat,lng);
            return bookstores.stream()
                    .map(Bookstore -> convertToBookstoreResponse(Bookstore,member))
                    .collect(Collectors.toList());
        }
        List <Bookstore> bookstores =bookstoreRepository.findByBookstoreCategoryByDistance(category,lat,lng);
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
        String modKeyword=bookstore.getKeyword();
        if(modKeyword.equals(" 일반")){
            modKeyword =" 일반서적";
        }

        return new BookstoreResponse(
                bookstore.getBookstoreId(),
                bookstore.getName(),
                bookstore.getRating(),
                modKeyword,
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
    public List<BookstoreResponse> getLikedBookstoresByCategory(Member member,BookstoreCategory category,double lat, double lng){
        String memberKey = "member:liked:bookstores:" + member.getMemberId();
        Set<String> bookstoreIds = redisTemplate.opsForSet().members(memberKey);

        List<Long> longBookstoreIds = bookstoreIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List <Bookstore> bookstores = bookstoreRepository.findAllByIdOrderByDistance(longBookstoreIds,lat,lng);
        if(category==null){
            return bookstores.stream()
                    .map(Bookstore -> convertToBookstoreResponse(Bookstore,member))
                    .collect(Collectors.toList());
        }else {
            return bookstores.stream()
                    .filter(bookstore -> bookstore.getBookstoreCategory() == category)
                    .map(Bookstore -> convertToBookstoreResponse(Bookstore, member))
                    .collect(Collectors.toList());
        }
    }

    public BookstoreDetailResponse getBookstoreDetail(Member member, Long bookstoreId){
        Bookstore bookstore = bookstoreRepository.findById(bookstoreId)
                .orElseThrow(() -> new EntityNotFoundException("해당 서점을 찾을 수 없습니다."));

        //찜 true,false
        boolean isLiked;
        if(member != null){
            isLiked = checkIfBookstoreLiked(member.getMemberId(),bookstore.getBookstoreId());
        }else{ //로그인 안한 사용자
            isLiked = false;
        }
        //일반 -> 일반서적
        String modKeyword=bookstore.getKeyword();
        if(modKeyword.equals(" 일반")){
            modKeyword =" 일반서적";
        }
        //번호 - 하이픈 추가
        String phone = bookstore.getPhone();
        if (phone != null) {
            String phone1, phone2, phone3;
            if (phone.startsWith("02")) {//서울
                phone1 = phone.substring(0, 2);
                phone2 = phone.substring(2, phone.length() - 4);
                phone3 = phone.substring(phone.length() - 4);
            }else if (phone.startsWith("0507")) { //안심번호
                phone1 = phone.substring(0, 4);
                phone2 = phone.substring(4, phone.length() - 4);
                phone3 = phone.substring(phone.length() - 4);
            }else {
                phone1 = phone.substring(0, 3);
                phone2 = phone.substring(3, phone.length() - 4);
                phone3 = phone.substring(phone.length() - 4);
            }
            phone = phone1 + "-" + phone2 + "-" + phone3;
        }
        //영업시간 -> Hours
        String hours = bookstore.getHours();
        Hours finalHours;
        if (hours != null) {
            Pattern weekdayPattern = Pattern.compile("평일개점마감시간\\s*:\\s*([^,토휴]*)");
            Pattern saturdayPattern = Pattern.compile("토요일개점마감시간\\s*:\\s*([^,일휴]*)");
            Pattern sundayPattern = Pattern.compile("일요일개점마감시간\\s*:\\s*([^,휴]*)");
            Pattern holidayPattern = Pattern.compile("휴무일\\s*:\\s*(.*)");

            String weekdayHours = extractMatch(weekdayPattern, hours);
            String saturdayHours = extractMatch(saturdayPattern, hours);
            String sundayHours = extractMatch(sundayPattern, hours);
            String holidays = extractMatch(holidayPattern, hours);

            if (holidays != null) {
                if (holidays.contains("주말")) {
                    saturdayHours = "휴무";
                    sundayHours = "휴무";
                }
                if (holidays.contains("월요일")) {
                    weekdayHours += " (월요일 휴무)";
                }
                if (holidays.contains("토요일")) {
                    saturdayHours = "휴무";
                }
                if (holidays.contains("일요일")) {
                    sundayHours = "휴무";
                }
            }
            finalHours = new Hours(weekdayHours, saturdayHours, sundayHours);
        }else{
            finalHours = null;
        }
        //redis - 찜한 서점 수
        String bookstoreKey = "bookstore:likes:" + bookstoreId;
        String likedCountStr = redisTemplate.opsForValue().get(bookstoreKey);

        // string -> int 형 변환
        int likedCount = 0;
        likedCount = likedCountStr != null ? Integer.parseInt(likedCountStr) : 0;


        return new BookstoreDetailResponse(
                bookstore.getBookstoreId(),
                bookstore.getName(),
                phone,
                finalHours,
                bookstore.getRating(),
                modKeyword,
                bookstore.getAddress().substring(8),
                bookstore.getDescription(),
                isLiked,
                likedCount
        );
    }
    private static String extractMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

}

