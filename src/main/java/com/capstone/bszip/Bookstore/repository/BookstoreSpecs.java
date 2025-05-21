package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookstoreSpecs {
    // 검색 키워드 - name/address 칼럼에서 키워드를 포함하는 서점 반환
    public static Specification<Bookstore> nameOrAddressContains(String searchK) {
        return (root, query, cb) -> {
            if (searchK == null || searchK.isEmpty()) return cb.conjunction();
            String pattern = "%" + searchK + "%";
            return cb.or(
                    cb.like(root.get("name"), pattern),
                    cb.like(root.get("address"), pattern)
            );
        };
    }
    // 필터 키워드 리스트 - keyword 칼럼에서 키워드를 포함하는 서점 반환
    public static Specification<Bookstore> keywordIn(List<String> bookstoreK) {
        return (root, query, cb) -> {
            if (bookstoreK == null || bookstoreK.isEmpty()) return cb.conjunction();

            // 리스트의 각 키워드에 대해 LIKE 조건 생성
            List<Predicate> predicates = new ArrayList<>();
            for (String k : bookstoreK) {
                // 키워드에서 공백 제거
                String processedK = k.replace(" ", "");

                /*DB의 keyword 컬럼에서 공백 제거 및 소문자 변환
                Expression<String> dbKeywordNoSpace = cb.function(
                        "REPLACE", String.class,
                        cb.lower(root.get("keyword")),
                        cb.literal(" "), cb.literal("")
                );*/
                // LIKE 조건 생성
                predicates.add(cb.like(root.get("keyword"), "%" + processedK + "%"));
            }

            // 모든 LIKE 조건을 OR로 결합 (여러 키워드 중 하나라도 일치하면 반환)
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    // 필터 지역 - address 칼럼에서 지역 해당하는 서점 반환
    public static Specification<Bookstore> regionContains(String region) {
        return (root, query, cb) -> {
            if (region == null || region.isEmpty()) return cb.conjunction();
            String pattern = "%" + region + "%";
            return cb.like(root.get("address"), pattern);
        };
    }
}
