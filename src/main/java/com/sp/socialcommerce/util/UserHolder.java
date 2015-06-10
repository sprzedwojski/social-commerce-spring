package com.sp.socialcommerce.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by szymon on 6/7/15.
 */
@Component
//@Scope("session")
public class UserHolder {

    private String uid;
    private List<String> ratedProductsIds;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getRatedProductsIds() {
        return ratedProductsIds;
    }

    public void setRatedProductsIds(List<String> ratedProductsIds) {
        this.ratedProductsIds = ratedProductsIds;
    }
}
