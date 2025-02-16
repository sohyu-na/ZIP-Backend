package com.capstone.bszip.Bookstore.service.dto;

import lombok.Data;

@Data
public class BookstoreResponse {
    private String name;
    //private double rating; 별점 _ 리뷰 후에
    private String category;
    private String phone;
    private String hours;
    private String address;
    private String description;
}
