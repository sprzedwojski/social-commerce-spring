package com.sp.socialcommerce.labels;

import com.sp.socialcommerce.neo4j.GraphConstants;
import org.neo4j.graphdb.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by szymon on 6/5/15.
 */
@Component
@Scope("session")
public class Product implements Label {

    private String namePl;
    private String nameEn;
    private String imageUrl;
    private String productUrl;
    private String descriptionPl;
    private String descriptionEn;
    private int id;
    private double price;
    private String category;

    private String rating;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String name() {
        return GraphConstants.Product.PRODUCT_LABEL;
    }

    public String getNamePl() {
        return namePl;
    }

    public void setNamePl(String namePl) {
        this.namePl = namePl;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescriptionPl() {
        return descriptionPl;
    }

    public void setDescriptionPl(String descriptionPl) {
        this.descriptionPl = descriptionPl;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
