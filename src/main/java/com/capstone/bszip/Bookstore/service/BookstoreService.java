package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.repository.BookstoreSpecs;
import com.capstone.bszip.Bookstore.service.dto.BookstoreDetailResponse;
import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import com.capstone.bszip.Bookstore.service.dto.Hours;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    private final RedisTemplate<String,String> redisTemplate;

    // 서점(Bookstore) 리스트 검색
    @Transactional(readOnly = true)
    public List<BookstoreResponse> searchBookstores(String searchK, List<String> bookstoreK, String region, String sortField, Member member,double lat, double lng) {
        Specification<Bookstore> spec = Specification.where(BookstoreSpecs.nameOrAddressContains(searchK))
                .and(BookstoreSpecs.keywordIn(bookstoreK))
                .and(BookstoreSpecs.regionContains(region));

        List<Bookstore> bookstores = switch (sortField) {
            case "rating" -> bookstoreRepository.findWithFiltersOrderByRating(spec);
            case "likes" -> bookstoreRepository.findWithFiltersOrderByLikes(spec);
            default -> bookstoreRepository.findWithFiltersOrderByDistance(spec, lat, lng);
        };
        return bookstores.stream()
                .map(Bookstore -> BookstoreResponse.from(Bookstore,isBookstoreLiked(member, Bookstore)))
                .collect(Collectors.toList());
    }
    private boolean isBookstoreLiked(Member member, Bookstore bookstore) {
        if (member == null) return false;
        String memberKey = "member:liked:bookstores:" + member.getMemberId();
        return redisTemplate.opsForSet().isMember(memberKey, bookstore.getBookstoreId().toString());
    }

    // 서점(Bookstore) 찜하기,찜취소
    public void toggleLikeBookstore(Long memberId, Long bookstoreId){
        String memberKey = "member:liked:bookstores:" + memberId;
        String bookstoreKey = "bookstore:likes:" + bookstoreId;

        if(redisTemplate.opsForSet().isMember(memberKey,bookstoreId.toString())){
            redisTemplate.opsForSet().remove(memberKey,bookstoreId.toString());//찜 취소
            redisTemplate.opsForValue().decrement(bookstoreKey); //찜한 수 -1
            redisTemplate.opsForZSet().incrementScore("trending:bookstores:weekly", String.valueOf(bookstoreId), -1);
        }
        else{
            redisTemplate.opsForSet().add(memberKey,bookstoreId.toString());//찜
            redisTemplate.opsForValue().increment(bookstoreKey); //찜한 수 +1
            redisTemplate.opsForZSet().incrementScore("trending:bookstores:weekly", String.valueOf(bookstoreId), 1);
        }
    }

    //찜한 서점 목록 조회
    @Transactional(readOnly = true)
    public List<BookstoreResponse> getLikedBookstoresByCategory(Member member ,double lat, double lng){
        String memberKey = "member:liked:bookstores:" + member.getMemberId();
        Set<String> bookstoreIds = redisTemplate.opsForSet().members(memberKey);

        List<Long> longBookstoreIds = bookstoreIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List <Bookstore> bookstores = bookstoreRepository.findAllByIdOrderByDistance(longBookstoreIds,lat,lng);

        return bookstores.stream()
                .map(Bookstore -> BookstoreResponse.from(Bookstore,isBookstoreLiked(member, Bookstore)))
                .collect(Collectors.toList());
    }

    //서점 상세 정보 조회
    @Transactional(readOnly = true)
    public BookstoreDetailResponse getBookstoreDetail(Member member, Long bookstoreId){
        Bookstore bookstore = bookstoreRepository.findById(bookstoreId)
                .orElseThrow(() -> new EntityNotFoundException("해당 서점을 찾을 수 없습니다."));

        // 데이터 처리
        // phone : 하이픈 추가
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
        // hours : Hours 객체 변환
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
        // keyword : 일반 -> 일반서적
        String keyword=bookstore.getKeyword();
        if(keyword.equals(" 일반")){
            keyword =" 일반서적";
        }
        // isLiked : 찜 여부 조회
        boolean isLiked = isBookstoreLiked(member,bookstore);

        // likedCount : 찜한 서점 수 조회 후 int 변환
        String likedCountStr = redisTemplate.opsForValue().get("bookstore:likes:" + bookstoreId);
        int likedCount = likedCountStr != null ? Integer.parseInt(likedCountStr) : 0;

        return  BookstoreDetailResponse.from(
                bookstore,
                phone,
                finalHours,
                keyword,
                isLiked,
                likedCount
        );
    }
    private static String extractMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    // 급상승 서점 이름 목록 조회
    @Transactional(readOnly = true)
    public List<String> getTrendingBookstoresNames(){
        // 1. redis 조회
        Set<String> topBookstoreIds = redisTemplate.opsForZSet()
                .reverseRange("trending:bookstores:weekly", 0, 9);
        // 2. List<Long> 으로 변환
        List<Long> idList = (topBookstoreIds == null) ? new ArrayList<>()
                : topBookstoreIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        // 3. 10개 미만 이면 lastweek 에서 추가
        if(idList.size() < 10){
            Set<String> lastWeekIds = redisTemplate.opsForZSet()
                    .reverseRange("trending:bookstores:lastweek", 0, 9);
            if (lastWeekIds != null && !lastWeekIds.isEmpty()) {
                List<Long> lastWeekIdList = lastWeekIds.stream()
                        .map(Long::valueOf)
                        .filter(id -> !idList.contains(id))
                        .collect(Collectors.toList());
                int need = 10 - idList.size();
                idList.addAll(lastWeekIdList.stream().limit(need).toList());
            }
        }
        // 4. 10개 미만이면 전체 서점에서 추가
        if (idList.size() < 10) {
            for (long i = 1; idList.size() < 10; i++) {
                if (!idList.contains(i)) {
                    idList.add(i);
                }
            }
        }
        // 5. id 리스트로 name 조회
        List<Object[]> idNamePairs = bookstoreRepository.findIdAndNameByIds(idList);
        Map<Long, String> idNameMap = idNamePairs.stream()
                .collect(Collectors.toMap(
                        row -> (Long)row[0],
                        row -> (String)row[1]
                ));
        List<String> orderedNames = idList.stream()
                .map(idNameMap::get)
                .collect(Collectors.toList());

        return orderedNames;
    }

}

