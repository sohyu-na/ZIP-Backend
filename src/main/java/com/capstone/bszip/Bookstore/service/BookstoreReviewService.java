package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreReview;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.repository.BookstoreReviewRepository;
import com.capstone.bszip.Bookstore.service.dto.BookstoreReviewRequest;
import com.capstone.bszip.Bookstore.service.dto.BookstoreReviewResponse;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.cloudinary.service.CloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookstoreReviewService {
    private final BookstoreRepository bookstoreRepository;
    private final BookstoreReviewRepository bookstoreReviewRepository;
    private final CloudinaryService cloudinaryService;;

    @Transactional
    public BookstoreReviewResponse createReview(Member member, BookstoreReviewRequest request, MultipartFile thumbnail){
        Bookstore bookstore = bookstoreRepository.findById(request.getBookstoreId())
                .orElseThrow(() -> new EntityNotFoundException("해당 서점을 찾을 수 없습니다."));

        String cloudinaryFolderName = "bookstores_review_image";
        BookstoreReview review = new BookstoreReview();
        review.setBookstore(bookstore);
        review.setMember(member);
        review.setRating(request.getRating());
        review.setText(request.getText());
        review.setImageUrl(cloudinaryService.uploadfile(thumbnail, cloudinaryFolderName));

        bookstoreReviewRepository.save(review);
        bookstore.updateRating(request.getRating()); //별점 업데이트

        return new BookstoreReviewResponse(
                review.getBookstoreReviewId(),
                member.getNickname(),
                bookstore.getRating(),
                review.getText(),
                review.getImageUrl(),
                review.getCreatedAt()
        );
    }
    // 서점별 리뷰 조회
    @Transactional(readOnly = true)
    public List<BookstoreReviewResponse> getReviewsByBookstoreId(Long bookstoreId, Sort sort){
        List<BookstoreReview> reviews = bookstoreReviewRepository.findReviewsByBookstoreId(bookstoreId,sort);
        return reviews.stream()
                .map(BookstoreReviewResponse::from)
                .collect(Collectors.toList());
    }

}
